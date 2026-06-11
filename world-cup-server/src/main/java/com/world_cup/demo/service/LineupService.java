package com.world_cup.demo.service;

import com.world_cup.demo.dto.MatchLineupSummaryDto;
import com.world_cup.demo.dto.MatchLineupsDto;
import com.world_cup.demo.entities.Match;
import com.world_cup.demo.mapper.LineupMapper;
import com.world_cup.demo.repositories.MatchRepository;
import com.world_cup.demo.service.cache.CacheSerializer;
import com.world_cup.demo.service.cache.LineupCache;
import com.world_cup.demo.util.apiUtils.FootballDataApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LineupService {

    private static final List<String> LINEUP_STATUSES = List.of("TIMED", "IN_PLAY");
    private static final long KICKOFF_LOOKBACK_HOURS = 2;
    private static final long KICKOFF_LOOKAHEAD_HOURS = 6;

    private final FootballDataApiUtil footballDataApiUtil;
    private final MatchRepository matchRepository;
    private final LineupCache lineupCache;
    private final CacheSerializer cacheSerializer;
    private final Logger logger = LoggerFactory.getLogger(LineupService.class);

    public LineupService(
            FootballDataApiUtil footballDataApiUtil,
            MatchRepository matchRepository,
            LineupCache lineupCache,
            CacheSerializer cacheSerializer
    ) {
        this.footballDataApiUtil = footballDataApiUtil;
        this.matchRepository = matchRepository;
        this.lineupCache = lineupCache;
        this.cacheSerializer = cacheSerializer;
    }

    public MatchLineupsDto getLineups(Integer matchId) {
        MatchLineupsDto cached = lineupCache.getLineups(matchId);
        if (cached != null) {
            return cached;
        }
        return fetchAndCacheLineups(matchId);
    }

    public MatchLineupSummaryDto getLineupSummary(Integer matchId) {
        MatchLineupSummaryDto cached = lineupCache.getLineupSummary(matchId);
        if (cached != null) {
            return cached;
        }

        MatchLineupsDto lineups = fetchAndCacheLineups(matchId);
        return lineups == null ? null : LineupMapper.toSummary(lineups);
    }

    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void syncUpcomingMatchLineups() {
        try {
            logger.info("Starting background scheduler cron: Fetching upcoming match lineups from football-data...");
            List<Match> candidates = matchRepository.findByStatusIn(LINEUP_STATUSES);
            Instant now = Instant.now();
            Instant windowStart = now.minus(KICKOFF_LOOKBACK_HOURS, ChronoUnit.HOURS);
            Instant windowEnd = now.plus(KICKOFF_LOOKAHEAD_HOURS, ChronoUnit.HOURS);

            List<Integer> matchIds = candidates.stream()
                    .filter(match -> isWithinLineupWindow(match.getMatchDate(), windowStart, windowEnd))
                    .map(Match::getMatchId)
                    .toList();

            if (matchIds.isEmpty()) {
                logger.info("No upcoming matches in lineup sync window");
                return;
            }

            ExecutorService executorService = Executors.newFixedThreadPool(3);
            for (Integer matchId : matchIds) {
                executorService.submit(() -> fetchAndCacheLineups(matchId));
            }
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
            logger.info("Finished lineup sync for {} matches", matchIds.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Lineup sync interrupted");
        } catch (Exception e) {
            logger.error("Failed to run lineup sync", e);
        }
    }

    private MatchLineupsDto fetchAndCacheLineups(Integer matchId) {
        if (matchId == null) {
            return null;
        }

        try {
            logger.info("Fetching lineups for match {}", matchId);
            String apiResponse = footballDataApiUtil.httpCallToApiForMatchLineups(matchId);
            MatchLineupsDto lineups = LineupMapper.fromFootballDataMatchResponse(apiResponse, matchId);

            if (lineups == null) {
                logger.warn("Skipping lineup cache for match {} — response empty or lineups not published yet", matchId);
                return null;
            }

            lineupCache.setLineups(matchId, cacheSerializer.convertObjectToJsonString(lineups));
            lineupCache.setLineupSummary(
                    matchId,
                    cacheSerializer.convertObjectToJsonString(LineupMapper.toSummary(lineups))
            );
            return lineups;
        } catch (Exception e) {
            logger.error("Failed to fetch lineups for match {}", matchId, e);
            return null;
        }
    }

    private boolean isWithinLineupWindow(String matchDate, Instant windowStart, Instant windowEnd) {
        if (matchDate == null || matchDate.isBlank()) {
            return false;
        }
        try {
            Instant kickoff = Instant.parse(matchDate);
            return !kickoff.isBefore(windowStart) && !kickoff.isAfter(windowEnd);
        } catch (Exception e) {
            return false;
        }
    }
}
