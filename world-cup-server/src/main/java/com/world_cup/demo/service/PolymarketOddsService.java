package com.world_cup.demo.service;

import com.world_cup.demo.client.PolymarketApiClient;
import com.world_cup.demo.comparators.WinnerTournamentComparator;
import com.world_cup.demo.dto.CleanedMarket;
import com.world_cup.demo.dto.CleanedPolymarketOdds;
import com.world_cup.demo.dto.PolyMarketResponse;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.repositories.TeamRepository;
import com.world_cup.demo.service.cache.CacheSerializer;
import com.world_cup.demo.service.cache.PolymarketOddsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PolymarketOddsService {

    private static final List<String> ADVANCEMENT_STAGES = List.of(
            "round-of-16",
            "quarterfinals",
            "semifinals",
            "final"
    );

    private final PolymarketApiClient polymarketApiClient;
    private final PolymarketOddsCache polymarketOddsCache;
    private final CacheSerializer cacheSerializer;
    private final TeamRepository teamRepository;
    private final Logger logger = LoggerFactory.getLogger(PolymarketOddsService.class);

    public PolymarketOddsService(PolymarketApiClient polymarketApiClient, PolymarketOddsCache polymarketOddsCache, TeamRepository teamRepository, CacheSerializer cacheSerializer){
        this.polymarketApiClient = polymarketApiClient;
        this.polymarketOddsCache = polymarketOddsCache;
        this.teamRepository = teamRepository;
        this.cacheSerializer = cacheSerializer;
    }

    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 4)
    public void fetchMarketOddsForGroupWinnersAndReachLaterStages(){
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup group winner and advancement odds from polymarket...");
            List<String> groups = teamRepository.findGroupNames().stream().map(group->group.toLowerCase().replace(" ","-")).toList();
            fetchAndCacheAllGroupsWinnerOdds(groups);
            fetchAndCacheAllAdvancementOdds();
        } catch (Exception e) {
            logger.error("Failed to run polymarket group winner / advancement odds sync", e);
        }
    }

    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 4)
    public void fetchAndSaveWinnerAndTopScorerOdds(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<String> teamsList = teamRepository.findAll().stream().map(Team::getTeamName).toList();
            logger.info("Starting background scheduler cron: Fetching World Cup winner and top scorer from polymarket...");

            CleanedPolymarketOdds topScorerOdds = buildMarketOdds(
                    polymarketApiClient.fetchTopScorer(),
                    objectMapper
            );
            if (topScorerOdds == null) {
                logger.warn("Skipping top scorer cache update — API response empty or unusable");
            } else {
                polymarketOddsCache.setTopScorerOdds(cacheSerializer.convertObjectToJsonString(topScorerOdds));
            }

            CleanedPolymarketOdds winnerOdds = buildWinnerOdds(
                    polymarketApiClient.fetchTournamentWinner(),
                    teamsList,
                    objectMapper
            );
            if (winnerOdds == null) {
                logger.warn("Skipping tournament winner cache update — API response empty or unusable");
            } else {
                polymarketOddsCache.setWinnerOdds(cacheSerializer.convertObjectToJsonString(winnerOdds));
            }
        } catch (Exception e) {
            logger.error("Failed to run polymarket odds sync", e);
        }
    }

    private void fetchAndCacheAllGroupsWinnerOdds(List<String> groups){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for(String group:groups){
            executorService.submit(()->fetchAndCacheGroupWinnerOdds(group));
        }
        executorService.shutdown();
    }

    private void fetchAndCacheAllAdvancementOdds() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (String stage : ADVANCEMENT_STAGES) {
            executorService.submit(() -> fetchAndCacheAdvancementOdds(stage));
        }
        executorService.shutdown();
    }

    private void fetchAndCacheGroupWinnerOdds(String groupName){
        logger.info("save winner group odds for group {} by thread id {}",groupName,Thread.currentThread().getId());
        ObjectMapper objectMapper = new ObjectMapper();
        CleanedPolymarketOdds groupsWinnerOdds = buildMarketOdds(
                polymarketApiClient.fetchGroupWinner(groupName),
                objectMapper
        );
        if (groupsWinnerOdds == null) {
            logger.warn("Skipping group winner cache update for {} — API response empty or unusable", groupName);
        } else {
            polymarketOddsCache.setGroupWinnerOdds(cacheSerializer.convertObjectToJsonString(groupsWinnerOdds),groupName);
        }
    }

    private void fetchAndCacheAdvancementOdds(String stage) {
        logger.info("save advancement odds for stage {} by thread id {}", stage, Thread.currentThread().getId());
        ObjectMapper objectMapper = new ObjectMapper();
        CleanedPolymarketOdds advancementOdds = buildMarketOdds(
                polymarketApiClient.fetchAdvancement(stage),
                objectMapper
        );
        if (advancementOdds == null) {
            logger.warn("Skipping advancement cache update for {} — API response empty or unusable", stage);
        } else {
            polymarketOddsCache.setAdvancementOdds(cacheSerializer.convertObjectToJsonString(advancementOdds), stage);
        }
    }

    /** Returns null when the API response is empty or cannot be parsed — caller should not write cache. */
    private CleanedPolymarketOdds buildMarketOdds(String apiResponse, ObjectMapper objectMapper) {
        if (apiResponse == null || apiResponse.isBlank()) {
            return null;
        }

        try {
            List<PolyMarketResponse> events = objectMapper.readValue(
                    apiResponse,
                    new TypeReference<List<PolyMarketResponse>>() {}
            );
            if (events == null || events.isEmpty()) {
                return null;
            }

            PolyMarketResponse event = events.get(0);
            if (event == null || event.markets() == null || event.markets().isEmpty()) {
                return null;
            }

            List<CleanedMarket> cleaned = event.markets().stream()
                    .filter(market -> market.outcomePrices() != null)
                    .map(market -> new CleanedMarket(
                            market.question(),
                            CleanedMarket.transformRawStringToDoubleList(market.outcomePrices())
                    ))
                    .sorted(new WinnerTournamentComparator())
                    .toList();

            if (cleaned.isEmpty()) {
                return null;
            }

            return new CleanedPolymarketOdds(event.title(), cleaned);
        } catch (Exception e) {
            logger.warn("Failed to parse polymarket response — skipping cache update", e);
            return null;
        }
    }

    /** Returns null when the API response is empty or cannot be parsed — caller should not write cache. */
    private CleanedPolymarketOdds buildWinnerOdds(
            String apiResponse,
            List<String> teamsList,
            ObjectMapper objectMapper
    ) {
        if (apiResponse == null || apiResponse.isBlank()) {
            return null;
        }

        try {
            List<PolyMarketResponse> events = objectMapper.readValue(
                    apiResponse,
                    new TypeReference<List<PolyMarketResponse>>() {}
            );
            if (events == null || events.isEmpty()) {
                return null;
            }

            PolyMarketResponse event = events.get(0);
            if (event == null || event.markets() == null || event.markets().isEmpty()) {
                return null;
            }

            List<CleanedMarket> cleaned = event.markets().stream()
                    .filter(market -> {
                        List<String> questionList = Arrays.asList(market.question().split(" "));
                        return market.outcomePrices() != null || teamsList.contains(questionList.get(1));
                    })
                    .map(market -> new CleanedMarket(
                            market.question(),
                            CleanedMarket.transformRawStringToDoubleList(market.outcomePrices())
                    ))
                    .sorted(new WinnerTournamentComparator())
                    .toList();

            if (cleaned.isEmpty()) {
                return null;
            }

            return new CleanedPolymarketOdds(event.title(), cleaned);
        } catch (Exception e) {
            logger.warn("Failed to parse tournament winner response — skipping cache update", e);
            return null;
        }
    }

    public CleanedPolymarketOdds getTournamentWinnerOdds(){
        try{
            return polymarketOddsCache.getWinnerOdds();
        } catch (Exception e) {
            return null;
        }
    }

    public CleanedPolymarketOdds getTopScorerOdds(){
        try{
            return polymarketOddsCache.getTopScorerOdds();
        } catch (Exception e) {
            return null;
        }
    }

    public CleanedPolymarketOdds getGroupWinnerOdds(String groupName){
        try{
            return polymarketOddsCache.getGroupWinnerOdds(groupName);
        } catch (Exception e) {
            return null;
        }
    }

    public CleanedPolymarketOdds getAdvancementOdds(String stage) {
        try {
            return polymarketOddsCache.getAdvancementOdds(stage);
        } catch (Exception e) {
            return null;
        }
    }
}
