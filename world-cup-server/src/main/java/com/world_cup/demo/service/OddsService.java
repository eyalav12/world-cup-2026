package com.world_cup.demo.service;

import com.world_cup.demo.dto.OddsGridDTO;
import com.world_cup.demo.dto.OddsSummaryDTO;
import com.world_cup.demo.dto.SportsbookOdds;
import com.world_cup.demo.entities.Match;
import com.world_cup.demo.entities.MatchOddsSnapshot;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.repositories.MatchOddsRepository;
import com.world_cup.demo.repositories.MatchRepository;
import com.world_cup.demo.repositories.TeamRepository;
import com.world_cup.demo.service.cache.CacheSerializer;
import com.world_cup.demo.service.cache.OddsCache;
import com.world_cup.demo.util.DateUtil;
import com.world_cup.demo.util.OddsTeamNameUtil;
import com.world_cup.demo.util.apiUtils.OddsApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.world_cup.demo.util.apiUtils.FootballDataApiUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.Instant;


@Service
public class OddsService {

    private OddsApiUtil oddsApiUtil;
    private MatchRepository matchRepository;
    private TeamRepository teamRepository;
    private OddsCache oddsCache;
    private MatchOddsRepository matchOddsRepository;
    private CacheSerializer cacheSerializer;
    private static final Logger logger = LoggerFactory.getLogger(OddsService.class);

    @Value("${oddsdata.api.token:}")
    private String oddsApiToken;

    public OddsService(
            OddsApiUtil oddsApiUtil,
            MatchRepository matchRepository,
            TeamRepository teamRepository,
            OddsCache oddsCache,
            MatchOddsRepository matchOddsRepository,
            CacheSerializer cacheSerializer) {
        this.oddsApiUtil = oddsApiUtil;
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.oddsCache = oddsCache;
        this.matchOddsRepository = matchOddsRepository;
        this.cacheSerializer = cacheSerializer;
    }

    public OddsSummaryDTO getOddsSummaryByTeamId(Integer matchId){
        try{
            OddsSummaryDTO cachedOdds = getCachedOddsSummary(matchId);
            if(cachedOdds != null){
                logger.info("Cache hit for football matchId: {}", matchId);
                return cachedOdds;
            }
            logger.warn("Odds not cached for football matchId: {}", matchId);
            return null;
        }
        catch(Exception e){
            logger.error("Failed to fetch odds summary for football matchId: " + matchId, e);
            return null;
        }
    }

    public OddsSummaryDTO getOddsSummaryByTeamNames(String teamA, String teamB, String matchDate) {
        try {
            Match match = resolveMatchByTeamNames(teamA, teamB, matchDate);
            if (match == null) {
                logger.warn("No match found for teams {} vs {}", teamA, teamB);
                return null;
            }

            OddsSummaryDTO cached = getCachedOddsSummary(match.getMatchId());
            if (cached != null) {
                logger.info("Cache hit for odds {} vs {} (football matchId={})", teamA, teamB, match.getMatchId());
                return cached;
            }

            logger.warn("Odds not cached for {} vs {} (football matchId={})", teamA, teamB, match.getMatchId());
            return null;
        } catch (Exception e) {
            logger.error("Failed to fetch odds summary for teams {} vs {}", teamA, teamB, e);
            return null;
        }
    }

    private Match resolveMatchByTeamNames(String teamA, String teamB, String matchDate) {
        if (teamA == null || teamB == null || teamA.isBlank() || teamB.isBlank()) {
            return null;
        }
        if (matchDate != null && !matchDate.isBlank()) {
            return matchRepository.findMatchByTeamNamesAndDate(teamA.trim(), teamB.trim(), matchDate.trim());
        }
        return matchRepository.findUpcomingMatchByTeamNames(teamA.trim(), teamB.trim());
    }

    private OddsSummaryDTO getCachedOddsSummary(Integer cacheKeyMatchId) {
        if (cacheKeyMatchId == null) {
            return null;
        }
        OddsSummaryDTO fromRedis = oddsCache.getCacheOdds(cacheKeyMatchId.toString());
        if (fromRedis != null) {
            return fromRedis;
        }
        return loadPersistedOddsSummary(cacheKeyMatchId);
    }

    private OddsSummaryDTO loadPersistedOddsSummary(Integer matchId) {
        return matchOddsRepository.findById(matchId)
                .map(row -> {
                    try {
                        OddsSummaryDTO summary = cacheSerializer.parseJson(
                                row.getSummaryJson(), OddsSummaryDTO.class);
                        if (summary != null) {
                            logger.info("DB odds snapshot hit for football matchId: {}", matchId);
                        }
                        return summary;
                    } catch (Exception e) {
                        logger.error("Failed to read persisted odds for matchId: {}", matchId, e);
                        return null;
                    }
                })
                .orElse(null);
    }

    private void persistOddsSnapshot(OddsSummaryDTO summary) {
        if (summary == null || summary.getMatchId() == null) {
            return;
        }
        try {
            String json = cacheSerializer.convertObjectToJsonString(summary);
            matchOddsRepository.save(
                    new MatchOddsSnapshot(summary.getMatchId(), json, Instant.now()));
        } catch (Exception e) {
            logger.error("Failed to persist odds snapshot for matchId: {}", summary.getMatchId(), e);
        }
    }


    @Scheduled(fixedRate = 1000 * 60 * 60 * 4) // Runs every 12 hours
    public void getFutureMatchesOddsAndFillCache() {
        if (oddsApiToken == null || oddsApiToken.isBlank()) {
            logger.warn(
                    "ODDSDATA_API_TOKEN is not set — skipping odds sync. "
                            + "Add the token to .env.prod and restart the server.");
            return;
        }
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup odds from API...");
            String apiResponse = oddsApiUtil.httpCallToApi();

            // 1. Process data structures & execute math conversions
            Set<String> dbTeamNames = teamRepository.findAll().stream()
                    .map(Team::getTeamName)
                    .collect(Collectors.toSet());
            List<OddsSummaryDTO> processedSummaries = processAndPrepareOddsData(apiResponse, dbTeamNames);

            if (processedSummaries.isEmpty()) {
                logger.warn(
                        "Odds API run complete, but zero matches were mapped to the database. "
                                + "Ensure matches are synced (Football Data API) and team names/dates align.");
                return;
            }

            // 2. Load the data architectures into Redis
            setOddsMatchesToCache(processedSummaries);
            logger.info("Successfully populated Redis layouts for {} matches.", processedSummaries.size());

        } catch (Exception e) {
            logger.error("Failed to run pipeline sync or save odds to cache", e);
        }
    }

    private void setOddsMatchesToCache(List<OddsSummaryDTO> processedSummaries) {
        for (OddsSummaryDTO summaryDto : processedSummaries) {
            Integer internalId = summaryDto.getMatchId();

            // Build the separate string keys
            String appUiKey = "odds:match:" + internalId;
            String agentKey = "odds:summary:" + internalId;

            try {
                // --- Layout A: Build simple Frontend UI List ---
                List<OddsGridDTO> frontendList = new ArrayList<>();
                for (SportsbookOdds bookOdds : summaryDto.getTopSportsbooks()) {
                    frontendList.add(new OddsGridDTO(bookOdds));
                }

                // Push frontend structure to Redis (Cached for 12 hours)
                oddsCache.cacheOdds(appUiKey, new ObjectMapper().writeValueAsString(frontendList));
                // --- Layout B: Build analytical Object structure for Agent ---
                // The summaryDto already contains the full analytical data object package.
                oddsCache.cacheOdds(agentKey, new ObjectMapper().writeValueAsString(summaryDto));

                persistOddsSnapshot(summaryDto);

                logger.debug("Successfully saved cache splits for match sequence ID: {}", internalId);

            } catch (Exception e) {
                logger.error("Failed to write data structures to Redis keys for match ID: " + internalId, e);
            }
        }
    }

    private List<Map<String,Map<String, Map<String,Integer>>>> extractDate(String apiResponse){
        ObjectMapper objectMapper=new ObjectMapper();
        List<Map<String,Map<String,Map<String,Integer>>>> res=new ArrayList<>();
        List<Map> fullMatchesOddsList = objectMapper.readValue(apiResponse, List.class);
        for(Map oddsMatchMap:fullMatchesOddsList){
            Map<String,Map<String,Map<String,Integer>>> map3=new HashMap<>();
            String uuid = (String)oddsMatchMap.get("id");
            String homeTeam = (String)oddsMatchMap.get("home_team");
            String awayTeam = (String)oddsMatchMap.get("away_team");
            String time = (String) oddsMatchMap.get("commence_time");

            Integer matchIdFromDb = findDbMatchIdByOddsApiId(uuid,homeTeam,awayTeam,time);
            if(matchIdFromDb == -1) continue;

            List<Map> bookmakers = (List<Map>) oddsMatchMap.get("bookmakers");
            Map<String,Map<String,Integer>> map2 = new HashMap<>();
            for(int i=0;i<3;i++){
                String title = (String) bookmakers.get(i).get("title");
                List<Map> markets = (List<Map>) bookmakers.get(i).get("markets");
                for(Map market:markets){
                    List<Map> outcomes = (List<Map>)market.get("outcomes");
                    Map<String,Integer> map1=new HashMap<>();
                    for(Map outcome:outcomes){
                        String name = (String) outcome.get("name");
                        Integer price = (Integer) outcome.get("price");
                        map1.put(name,price);
                    }
                    map2.put(title,map1);

                }
                map3.put(uuid,map2);

            }

            res.add(map3);
        }
        return res;
    }



    public List<OddsSummaryDTO> processAndPrepareOddsData(String apiResponse, Set<String> dbTeamNames) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<OddsSummaryDTO> generatedSummaries = new ArrayList<>();

        try {
            List<Map> fullMatchesOddsList = objectMapper.readValue(apiResponse, List.class);
            int apiEventCount = fullMatchesOddsList.size();

            for (Map oddsMatchMap : fullMatchesOddsList) {
                String uuid = (String) oddsMatchMap.get("id");
                String homeTeam = OddsTeamNameUtil.normalize((String) oddsMatchMap.get("home_team"), dbTeamNames);
                String awayTeam = OddsTeamNameUtil.normalize((String) oddsMatchMap.get("away_team"), dbTeamNames);
                String time = (String) oddsMatchMap.get("commence_time");

                Integer matchIdFromDb = findDbMatchIdByOddsApiId(uuid, homeTeam, awayTeam, time);
                if (matchIdFromDb == null || matchIdFromDb == -1) continue;

                List<Map> bookmakers = (List<Map>) oddsMatchMap.get("bookmakers");
                if (bookmakers == null || bookmakers.isEmpty()) continue;

                List<SportsbookOdds> topThreeBooks = new ArrayList<>();

                double totalHomePct = 0;
                double totalDrawPct = 0;
                double totalAwayPct = 0;

                int maxAwayPrice = Integer.MIN_VALUE;
                String maxAwayBookmaker = "";
                int minHomePrice = Integer.MAX_VALUE;
                String minHomeBookmaker = "";

                // Safely read up to 3 bookmakers
                int booksToProcess = Math.min(bookmakers.size(), 3);
                for (int i = 0; i < booksToProcess; i++) {
                    Map bookmakerMap = bookmakers.get(i);
                    String title = (String) bookmakerMap.get("title");
                    List<Map> markets = (List<Map>) bookmakerMap.get("markets");

                    if (markets == null || markets.isEmpty()) continue;

                    List<Map> outcomes = (List<Map>) markets.get(0).get("outcomes");

                    int homeOdds = 0, awayOdds = 0, drawOdds = 0;

                    for (Map outcome : outcomes) {
                        String name = (String) outcome.get("name");
                        int price = ((Number) outcome.get("price")).intValue();

                        if (name.equals(homeTeam)) {
                            homeOdds = price;
                        } else if (name.equals(awayTeam)) {
                            awayOdds = price;
                        } else if (name.equalsIgnoreCase("Draw")) {
                            drawOdds = price;
                        }
                    }

                    // Run Probability Calculations
                    double homePct = calculateImpliedProbability(homeOdds);
                    double drawPct = calculateImpliedProbability(drawOdds);
                    double awayPct = calculateImpliedProbability(awayOdds);

                    totalHomePct += homePct;
                    totalDrawPct += drawPct;
                    totalAwayPct += awayPct;

                    // Track Best Extremes
                    if (awayOdds > maxAwayPrice) {
                        maxAwayPrice = awayOdds;
                        maxAwayBookmaker = title;
                    }
                    if (homeOdds < minHomePrice) {
                        minHomePrice = homeOdds;
                        minHomeBookmaker = title;
                    }

                    topThreeBooks.add(new SportsbookOdds(title, homeOdds, Math.round(homePct * 10.0) / 10.0,
                            drawOdds, Math.round(drawPct * 10.0) / 10.0,
                            awayOdds, Math.round(awayPct * 10.0) / 10.0));
                }

                if (topThreeBooks.isEmpty()) continue;

                // Finalize Averages calculations
                double avgHomePct = totalHomePct / booksToProcess;
                double avgDrawPct = totalDrawPct / booksToProcess;
                double avgAwayPct = totalAwayPct / booksToProcess;

                Map<String, Object> marketAverage = new HashMap<>();
                marketAverage.put("home_win_pct", Math.round(avgHomePct * 10.0) / 10.0);
                marketAverage.put("draw_pct", Math.round(avgDrawPct * 10.0) / 10.0);
                marketAverage.put("away_win_pct", Math.round(avgAwayPct * 10.0) / 10.0);
                marketAverage.put("home_price", convertProbabilityToAmericanOdds(avgHomePct));
                marketAverage.put("draw_price", convertProbabilityToAmericanOdds(avgDrawPct));
                marketAverage.put("away_price", convertProbabilityToAmericanOdds(avgAwayPct));

                Map<String, Object> marketExtremes = new HashMap<>();
                marketExtremes.put("best_away_payout", Map.of("bookmaker", maxAwayBookmaker, "price", maxAwayPrice));
                marketExtremes.put("best_home_deal", Map.of("bookmaker", minHomeBookmaker, "price", minHomePrice));

                // Instantiating the clean target structure
                OddsSummaryDTO fullSummary = new OddsSummaryDTO(matchIdFromDb, marketAverage, marketExtremes, topThreeBooks);
                generatedSummaries.add(fullSummary);
            }

            logger.info("Odds mapping: {} API events → {} cached match summaries", apiEventCount, generatedSummaries.size());

        } catch (Exception e) {
            logger.error("Error processing odds structures ", e);
        }

        return generatedSummaries;
    }

    // Mathematical Conversion Math Frameworks
    private double calculateImpliedProbability(int americanOdds) {
        if (americanOdds < 0) {
            return ((double) (-americanOdds) / (-americanOdds + 100)) * 100;
        } else {
            return (100.0 / (americanOdds + 100)) * 100;
        }
    }

    private int convertProbabilityToAmericanOdds(double probability) {
        if (probability >= 50.0) {
            return (int) -Math.round((probability / (100.0 - probability)) * 100.0);
        } else {
            return (int) Math.round(((100.0 - probability) / probability) * 100.0);
        }
    }





    private Integer findDbMatchIdByOddsApiId(String oddsApiId, String homeTeam, String awayTeam, String commenceTime) {
        try {
            Match matchByOddsApiId = matchRepository.findMatchByOddsApiId(oddsApiId);
            if (matchByOddsApiId != null) {
                return matchByOddsApiId.getMatchId();
            }

            String matchDate = DateUtil.toTournamentDateKey(commenceTime);
            if (matchDate == null) {
                return -1;
            }

            Match match = matchRepository.findMatchByTeamNamesAndDate(homeTeam, awayTeam, matchDate);
            if (match != null) {
                matchRepository.updateMatchWithOddsApiId(match.getMatchId(), oddsApiId);
                return match.getMatchId();
            }

            logger.warn("No DB match for odds event {} vs {} on {} (tournament date)", homeTeam, awayTeam, matchDate);
            return -1;
        } catch (Exception e) {
            logger.error("failed to find match in db ", e);
            return -1;
        }
    }

    // kept for legacy callers in extractDate
    private static String toDateOnly(String commenceTime) {
        return DateUtil.toTournamentDateKey(commenceTime);
    }
}
