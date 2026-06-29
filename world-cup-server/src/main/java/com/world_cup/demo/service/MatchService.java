package com.world_cup.demo.service;

import com.world_cup.demo.dto.FinishedGame;
import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.entities.Match;
import com.world_cup.demo.mapper.MatchMapper;
import com.world_cup.demo.publisher.RabbitMQProducer;
import com.world_cup.demo.repositories.MatchRepository;
import com.world_cup.demo.service.cache.MatchesCache;
import com.world_cup.demo.util.apiUtils.FootballDataApiUtil;
import com.world_cup.demo.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);
    private FootballDataApiUtil footballDataApiUtil;
    private DateUtil dateUtil;
    private MatchRepository matchRepository;
    private RabbitMQProducer rabbitMQProducer;
    private MatchesCache matchesCache;
    private final Integer NUMBER_OF_DAYS_AHEAD = 14;
    private final LocalDate TOURNAMENT_START_DATE = LocalDate.of(2026,06,11);

    private static final Map<String, Integer> STATUS_SORT_ORDER = Map.of(
            "IN_PLAY", 0,
            "TIMED", 1,
            "FINISHED", 2,
            "POSTPONED", 3
    );

    public MatchService(MatchRepository matchRepository, FootballDataApiUtil footballDataApiUtil, DateUtil dateUtil, MatchesCache matchesCache, RabbitMQProducer rabbitMQProducer){
        this.matchRepository = matchRepository;
        this.footballDataApiUtil = footballDataApiUtil;
        this.dateUtil = dateUtil;
        this.matchesCache = matchesCache;
        this.rabbitMQProducer = rabbitMQProducer;
    }


    @Scheduled(fixedRate = 1000*60)
    public void liveMatchesFetchAndUpdateWS(){
        LocalDate fromDate = LocalDate.now();
        //fetch limit to 1 match date. games sorted by match date. calculate time between it and now. if its less than or more an hour- do calls
        //convert the match date from string to format of now to calc it.
        //call the api (not with live only, since we want to see over also).
        //for each game that got- its a update- if a goal- can send to other web socket of notifications. if update - send, else no
    }

    @Scheduled(fixedRate = 1000*60*60*3)
    public void getFutureMatchesAndFillDB(){
        syncUpcomingMatchesFromApi();
    }

    /** Loads TIMED matches for the next 14 days from Football Data API into Postgres. */
    public void syncUpcomingMatchesFromApi() {
        LocalDate fromDate = LocalDate.now();
        if(fromDate.isBefore(TOURNAMENT_START_DATE)){
            fromDate = TOURNAMENT_START_DATE;
        }
        LocalDate toDate = fromDate.plusDays(NUMBER_OF_DAYS_AHEAD);
        List<MatchDto> matchesInDatesRange = getMatchesByDateFromApi(fromDate, toDate);
        saveOrUpdateMatchesToDB(matchesInDatesRange);
        matchesCache.refreshFromSyncedMatches(matchesInDatesRange);
    }


    @Scheduled(fixedRate = 1000*60*15)
    public void getCurrentMatchesResultsAndUpdate(){
        try{
            LocalDate nowDate = LocalDate.now();
            LocalDate prevDate = nowDate.minusDays(1);

            List<MatchDto> matchesToUpdate = getMatchesByDateFromApi(prevDate, nowDate);

            List<MatchDto> finishedGames = matchesToUpdate.stream().filter(matchDto -> matchDto.getStatus().equals("FINISHED")).toList();

            saveOrUpdateMatchesToDB(matchesToUpdate);
            matchesCache.refreshFromSyncedMatches(matchesToUpdate);

            for (MatchDto finishedGame : finishedGames) {
                if (matchesCache.getIsMatchFinished(finishedGame.getMatchId())) {
                    continue;
                }
                matchesCache.prependRecentFinished(finishedGame);
                matchesCache.setFinishedGameById(finishedGame.getMatchId());
                rabbitMQProducer.sendMessage(new FinishedGame(finishedGame.getMatchId(), finishedGame.getResult()));
            }
        }
        catch(Exception e){
            logger.error("error in fetch or update current or ended matches ",e);
        }
    }

    public void saveOrUpdateMatchesToDB(List<MatchDto> matches){
        if(matches == null) return;
        for(MatchDto matchDto:matches){
            try{
                Match existedMatch = matchRepository.findByMatchId(matchDto.getMatchId());
                if(existedMatch == null){
                    matchRepository.save(MatchMapper.toEntity(matchDto));
                }

                else{
                    MatchMapper.applyUpdate(existedMatch, matchDto);
                    matchRepository.save(existedMatch);
                }

            }
            catch(Exception e){
                logger.error("failed to save match: " + matchDto.getMatchId(),e.getMessage());
            }
        }
    }

    public List<MatchDto> getMatchesByDate(LocalDate fromDate, LocalDate toDate, String status) {
        if (toDate == null) {
            toDate = fromDate;
        }
        try {
            List<MatchDto> matches = getMatchesByDateUncached(fromDate);
            return sortMatchesByStatusAndTime(filterByStatus(matches, status));
        } catch (Exception e) {
            logger.error("failed to get matches from db or cache ", e.getMessage());
            return Collections.emptyList();
        }
    }

    /** Single-date lookup (toDate accepted for API compat but only fromDate is used). */
    public List<MatchDto> getMatchesByDate(LocalDate fromDate, LocalDate toDate) {
        return getMatchesByDate(fromDate, toDate, null);
    }

    private List<MatchDto> getMatchesByDateUncached(LocalDate fromDate) {
        List<MatchDto> cachedMatchesByDate = matchesCache.getCachedMatchesByDate(fromDate);
        if (cachedMatchesByDate == null) {
            logger.info("cache miss -------- data of matches is fetched from db");
            List<Match> matchesByDate = matchRepository.findMatchesByDate(fromDate.toString());
            matchesCache.putMatchesByDate(matchesByDate);
            return sortMatchesByStatusAndTime(
                    matchesByDate.stream().map(MatchMapper::toDto).toList()
            );
        }
        logger.info("cache hit -------- data of matches is fetched from redis");
        return sortMatchesByStatusAndTime(cachedMatchesByDate);
    }

    public MatchDto getMatchByMatchId(Integer matchId) {
        if (matchId == null) {
            return null;
        }
        try {
            Match match = matchRepository.findByMatchId(matchId);
            return match == null ? null : MatchMapper.toDto(match);
        } catch (Exception e) {
            logger.error("failed to get match by id {}", matchId, e);
            return null;
        }
    }

    public List<MatchDto> getRecentFinishedMatches(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        try {
            List<MatchDto> cached = matchesCache.getRecentFinished(safeLimit);
            if (cached != null && !cached.isEmpty()) {
                return sortMatchesByStatusAndTime(cached);
            }
            List<Match> fromDb = matchRepository.findRecentFinishedMatches(safeLimit);
            List<MatchDto> dtos = sortMatchesByStatusAndTime(
                    fromDb.stream().map(MatchMapper::toDto).toList()
            );
            if (!dtos.isEmpty()) {
                matchesCache.seedRecentFinished(dtos);
            }
            return dtos;
        } catch (Exception e) {
            logger.error("failed to get recent finished matches", e);
            return Collections.emptyList();
        }
    }

    public List<MatchDto> getMatchesByTeamName(String teamName, String status, Integer limit) {
        String effectiveStatus = (status == null || status.isBlank()) ? "TIMED" : status.trim();
        try {
            if ("TIMED".equalsIgnoreCase(effectiveStatus)) {
                return applyLimit(
                        sortMatchesByStatusAndTime(getUpcomingMatchesByTeamName(teamName)),
                        limit
                );
            }
            List<Match> matches;
            if (limit != null && limit > 0) {
                matches = matchRepository.findMatchesByTeamNameAndStatus(teamName, effectiveStatus, limit);
            } else {
                matches = matchRepository.findMatchesByTeamNameAndStatusAll(teamName, effectiveStatus);
            }
            if (matches == null || matches.isEmpty()) {
                return Collections.emptyList();
            }
            return sortMatchesByStatusAndTime(
                    matches.stream().map(MatchMapper::toDto).toList()
            );
        } catch (Exception e) {
            logger.error("error in get matches by team {} status {}", teamName, effectiveStatus, e);
            return Collections.emptyList();
        }
    }

    public List<MatchDto> getMatchesByGroupName(String groupName, String status, Integer limit) {
        try {
            List<MatchDto> matches = getUpcomingMatchesByGroupName(groupName);
            List<MatchDto> filtered = filterByStatus(matches, status);
            return applyLimit(sortMatchesByStatusAndTime(filtered), limit);
        } catch (Exception e) {
            logger.error("failed to find matches by group name {}", groupName, e);
            return Collections.emptyList();
        }
    }

    private List<MatchDto> filterByStatus(List<MatchDto> matches, String status) {
        if (matches == null || matches.isEmpty() || status == null || status.isBlank()) {
            return matches == null ? Collections.emptyList() : matches;
        }
        Set<String> allowed = Arrays.stream(status.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        return matches.stream()
                .filter(m -> m.getStatus() != null && allowed.contains(m.getStatus()))
                .toList();
    }

    private List<MatchDto> applyLimit(List<MatchDto> matches, Integer limit) {
        if (matches == null || matches.isEmpty() || limit == null || limit <= 0) {
            return matches == null ? Collections.emptyList() : matches;
        }
        return matches.size() > limit ? matches.subList(0, limit) : matches;
    }

    private List<MatchDto> getMatchesByDateFromApi(LocalDate fromDate,LocalDate toDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String response = footballDataApiUtil.httpCallToApi(dateUtil.localDateObjectToString(fromDate, formatter), dateUtil.localDateObjectToString(toDate, formatter));
        List<MatchDto> matchDtos = footballDataApiUtil.convertApiResponseToDtoMatchResult(response);
        return matchDtos;
    }

    private void addMatchDateSetToRedis(LocalDate localDate){

    }

    private void addMatchDateHashToRedis(MatchDto matchDto){

    }

    private void addMatchesByDateToDB(List<MatchDto> matchDtos){

    }

    public List<MatchDto> getUpcomingMatchesByGroupName(String groupName){
        try{
            List<Match> matchesByGroupName = matchRepository.findMatchesByGroupName(groupName);
            if(matchesByGroupName == null) return Collections.emptyList();
            return sortMatchesByStatusAndTime(dedupeByMatchId(
                    matchesByGroupName.stream().map(MatchMapper::toDto).toList()
            ));
        }
        catch(Exception e){
            logger.error("failed to find matches by group name ",groupName,e);
            return Collections.emptyList();
        }
    }

    public List<MatchDto> getUpcomingMatchesByTeamName(String teamName){
        try{
            List<Match> upcomingMatchesByTeamName = matchRepository.findUpcomingMatchesByTeamName(teamName);
            if(upcomingMatchesByTeamName == null || upcomingMatchesByTeamName.isEmpty()){
                return Collections.emptyList();
            }
            return upcomingMatchesByTeamName.stream().filter(upcomingMatchByTeamName -> {
                LocalDate today = dateUtil.todayInTournamentZone();
                String matchDate = upcomingMatchByTeamName.getMatchDate();
                LocalDate kickoffDay = dateUtil.parseInstantStringToLocalDate(matchDate);
                return !kickoffDay.isBefore(today);
            }).map(MatchMapper::toDto).toList();
        }
        catch (Exception e){
            logger.error("error in get upcoming matches ",teamName,e);
            return Collections.emptyList();
        }
    }


    private List<MatchDto> dedupeByMatchId(List<MatchDto> matches) {
        if (matches == null || matches.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, MatchDto> byId = new LinkedHashMap<>();
        for (MatchDto match : matches) {
            if (match.getMatchId() != null) {
                byId.putIfAbsent(match.getMatchId(), match);
            }
        }
        return new ArrayList<>(byId.values());
    }

    private int statusSortRank(String status) {
        if (status == null) {
            return 50;
        }
        return STATUS_SORT_ORDER.getOrDefault(status, 50);
    }

    private List<MatchDto> sortMatchesByStatusAndTime(List<MatchDto> matches) {
        if (matches == null || matches.isEmpty()) {
            return Collections.emptyList();
        }
        List<MatchDto> copy = new ArrayList<>(matches);
        copy.sort((a, b) -> {
            int statusCmp = Integer.compare(statusSortRank(a.getStatus()), statusSortRank(b.getStatus()));
            if (statusCmp != 0) {
                return statusCmp;
            }
            Instant ta = safeParseInstant(a.getMatchDate());
            Instant tb = safeParseInstant(b.getMatchDate());
            if ("FINISHED".equals(a.getStatus())) {
                return tb.compareTo(ta);
            }
            return ta.compareTo(tb);
        });
        return copy;
    }

    private Instant safeParseInstant(String matchDate) {
        if (matchDate == null || matchDate.isBlank()) {
            return Instant.EPOCH;
        }
        try {
            return dateUtil.parseInstant(matchDate);
        } catch (Exception e) {
            return Instant.EPOCH;
        }
    }


}
