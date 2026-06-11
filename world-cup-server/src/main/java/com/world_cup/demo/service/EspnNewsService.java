package com.world_cup.demo.service;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.NewsSummaryDto;
import com.world_cup.demo.dto.espn.EspnNewsApiResponse;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.mapper.EspnNewsMapper;
import com.world_cup.demo.mapper.NewsSummaryMapper;
import com.world_cup.demo.repositories.TeamRepository;
import com.world_cup.demo.service.cache.CacheSerializer;
import com.world_cup.demo.service.cache.EspnNewsCache;
import com.world_cup.demo.util.apiUtils.EspnApiUtil;
import com.world_cup.demo.util.apiUtils.EspnTeamIdResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class EspnNewsService {

    private static final long ESPN_API_DELAY_MS = 1200L;

    private final EspnApiUtil espnApiUtil;
    private final EspnTeamIdResolver espnTeamIdResolver;
    private final EspnNewsCache espnNewsCache;
    private final TeamRepository teamRepository;
    private final CacheSerializer cacheSerializer;
    private static final Logger logger = LoggerFactory.getLogger(EspnNewsService.class);

    public EspnNewsService(
            EspnApiUtil espnApiUtil,
            EspnTeamIdResolver espnTeamIdResolver,
            EspnNewsCache espnNewsCache,
            TeamRepository teamRepository,
            CacheSerializer cacheSerializer
    ) {
        this.espnApiUtil = espnApiUtil;
        this.espnTeamIdResolver = espnTeamIdResolver;
        this.espnNewsCache = espnNewsCache;
        this.teamRepository = teamRepository;
        this.cacheSerializer = cacheSerializer;
    }

    public NewsResponse getGeneralNews() {
        return espnNewsCache.getGeneralNews();
    }

    public NewsResponse getNewsByTeam(String teamName) {
        return espnNewsCache.getNewsForTeam(teamName);
    }

    public NewsSummaryDto getGeneralNewsSummary() {
        return espnNewsCache.getGeneralNewsSummary();
    }

    public NewsSummaryDto getNewsSummaryByTeam(String teamName) {
        return espnNewsCache.getNewsSummaryForTeam(teamName);
    }

    private void fetchTeamNewsAndSaveToCache(String teamName, String espnTeamId) {
        logger.info("Fetching ESPN news for {} (espnTeamId={})", teamName, espnTeamId);
        String apiResponse = espnApiUtil.httpCallToApiForTeamNews(espnTeamId);
        NewsResponse newsResponse = parseEspnNewsResponse(apiResponse);

        if (newsResponse == null || newsResponse.articles() == null || newsResponse.articles().isEmpty()) {
            logger.warn("Skipping ESPN cache for {} — response empty or unusable", teamName);
            return;
        }

        espnNewsCache.setNewsForTeam(teamName, cacheSerializer.convertObjectToJsonString(newsResponse));

        NewsSummaryDto summary = NewsSummaryMapper.toSummary(
                newsResponse,
                teamName,
                NewsSummaryMapper.TEAM_AGENT_HEADLINE_LIMIT
        );
        espnNewsCache.setNewsSummaryForTeam(teamName, cacheSerializer.convertObjectToJsonString(summary));
    }

    private void fetchNewsForAllTeamsSequential(List<String> teamNames, Map<String, String> espnTeamIdsByTeamName) {
        for (String teamName : teamNames) {
            try {
                String espnTeamId = espnTeamIdsByTeamName.get(teamName);
                if (espnTeamId == null) {
                    logger.warn("Skipping ESPN news fetch for {} — no ESPN team id", teamName);
                    continue;
                }

                fetchTeamNewsAndSaveToCache(teamName, espnTeamId);
                Thread.sleep(ESPN_API_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("ESPN team news sync interrupted");
                return;
            } catch (Exception e) {
                logger.error("Failed to fetch ESPN news for {}", teamName, e);
            }
        }
    }

    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 )
    public void getTeamsNewsAndFillCache() {
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup all teams news from ESPN...");
            List<String> allTeamsNames = teamRepository.findAll().stream().map(Team::getTeamName).toList();
            String teamsApiResponse = espnApiUtil.httpCallToApiForTeams();
            Map<String, String> espnTeamIdsByTeamName = espnTeamIdResolver.resolveEspnTeamIds(allTeamsNames, teamsApiResponse);
            fetchNewsForAllTeamsSequential(allTeamsNames, espnTeamIdsByTeamName);
        } catch (Exception e) {
            logger.error("Failed to run ESPN team news sync or save to cache", e);
        }
    }

    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 6)
    public void getGeneralNewsAndFillCache() {
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup general news from ESPN...");
            String apiResponse = espnApiUtil.httpCallToApiForGeneralNews();

            if (apiResponse == null || apiResponse.isBlank()) {
                logger.warn("ESPN general news run complete, but no data returned from the call.");
                return;
            }

            NewsResponse newsResponse = parseEspnNewsResponse(apiResponse);
            if (newsResponse == null || newsResponse.articles() == null || newsResponse.articles().isEmpty()) {
                logger.warn("Skipping ESPN general news cache update — response empty or unusable");
                return;
            }

            espnNewsCache.setGeneralNews(cacheSerializer.convertObjectToJsonString(newsResponse));

            NewsSummaryDto summary = NewsSummaryMapper.toSummary(
                    newsResponse,
                    null,
                    NewsSummaryMapper.GENERAL_AGENT_HEADLINE_LIMIT
            );
            espnNewsCache.setGeneralNewsSummary(cacheSerializer.convertObjectToJsonString(summary));
            logger.info("Successfully populated Redis layouts for ESPN general news.");

        } catch (Exception e) {
            logger.error("Failed to run ESPN general news sync or save to cache", e);
        }
    }

    private NewsResponse parseEspnNewsResponse(String apiResponse) {
        if (apiResponse == null || apiResponse.isBlank()) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EspnNewsApiResponse espnResponse = objectMapper.readValue(apiResponse, EspnNewsApiResponse.class);
            NewsResponse newsResponse = EspnNewsMapper.toNewsResponse(espnResponse);

            if (newsResponse.articles() == null || newsResponse.articles().isEmpty()) {
                return null;
            }

            return newsResponse;
        } catch (Exception e) {
            logger.warn("Failed to parse ESPN news response — skipping cache update", e);
            return null;
        }
    }
}
