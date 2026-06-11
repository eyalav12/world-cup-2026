package com.world_cup.demo.service;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.NewsSummaryDto;
import com.world_cup.demo.entities.Team;
import com.world_cup.demo.mapper.NewsSummaryMapper;
import com.world_cup.demo.repositories.TeamRepository;
import com.world_cup.demo.service.cache.CacheSerializer;
import com.world_cup.demo.service.cache.NewsCache;
import com.world_cup.demo.util.apiUtils.NewsDataApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NewsService {

    private static final long NEWS_API_DELAY_MS = 1200L;
    private final CacheSerializer cacheSerializer;

    private NewsDataApiUtil newsDataApiUtil;
    private NewsCache newsCache;
    private TeamRepository teamRepository;
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    public NewsService(NewsDataApiUtil newsDataApiUtil, NewsCache newsCache, TeamRepository teamRepository, CacheSerializer cacheSerializer){
        this.newsDataApiUtil = newsDataApiUtil;
        this.newsCache = newsCache;
        this.teamRepository = teamRepository;
        this.cacheSerializer = cacheSerializer;
    }

    public NewsResponse getGeneralNews(){
        NewsResponse generalNews = newsCache.getGeneralNews();
        return generalNews;
    }

    public NewsResponse getNewsByTeam(String teamName){
        NewsResponse teamNewsResponse = newsCache.getNewsForTeam(teamName);
        return teamNewsResponse;
    }

    public NewsSummaryDto getGeneralNewsSummary() {
        return newsCache.getGeneralNewsSummary();
    }

    public NewsSummaryDto getNewsSummaryByTeam(String teamName) {
        return newsCache.getNewsSummaryForTeam(teamName);
    }

    private void getNewsByTeamNameAndSaveToCache(String teamName) {
        logger.info("Fetching NewsAPI news for {}", teamName);
        String apiResponse = newsDataApiUtil.httpCallToApiForSpecificTeamNews(teamName);
        NewsResponse newsResponse = convertApiResponseToNewsObject(apiResponse);

        if (!"ok".equals(newsResponse.status())
                || newsResponse.articles() == null
                || newsResponse.articles().isEmpty()) {
            logger.warn("Skipping NewsAPI cache for {} — status={}", teamName, newsResponse.status());
            return;
        }

        newsCache.setNewsForTeam(teamName, cacheSerializer.convertObjectToJsonString(newsResponse));

        NewsSummaryDto summary = NewsSummaryMapper.toSummary(
                newsResponse,
                teamName,
                NewsSummaryMapper.TEAM_AGENT_HEADLINE_LIMIT
        );
        newsCache.setNewsSummaryForTeam(teamName, cacheSerializer.convertObjectToJsonString(summary));
    }

    private void getNewsForAllTeamsSequential(List<String> teamNames) {
        for (String teamName : teamNames) {
            try {
                getNewsByTeamNameAndSaveToCache(teamName);
                Thread.sleep(NEWS_API_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("NewsAPI team news sync interrupted");
                return;
            } catch (Exception e) {
                logger.error("Failed to fetch NewsAPI news for {}", teamName, e);
            }
        }
    }

    private void getNewsForAllTeams(List<String> teamNames){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(String teamName:teamNames){
            executorService.execute(()-> getNewsByTeamNameAndSaveToCache(teamName));
        }
        executorService.shutdown();

    }



    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    public void getTeamsNewsAndFillCache(){
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup all teams news from NewsAPI...");
            List<String> allTeamsNames = teamRepository.findAll().stream().map(Team::getTeamName).toList();
            getNewsForAllTeamsSequential(allTeamsNames);

        } catch (Exception e) {
            logger.error("Failed to run NewsAPI team news sync or save to cache", e);
        }
    }



    @Async
    @Scheduled(fixedRate = 1000 * 60 * 60 * 6)
    public void getGeneralNewsAndFillCache(){
        try {
            logger.info("Starting background scheduler cron: Fetching World Cup general news from NewsAPI...");
            String apiResponse = newsDataApiUtil.httpCallToApiForGeneralNews();

            if (apiResponse.isEmpty()) {
                logger.warn("NewsAPI run complete, but not found data from the call.");
                return;
            }
            NewsResponse newsResponse = convertApiResponseToNewsObject(apiResponse);

            if (!"ok".equals(newsResponse.status())
                    || newsResponse.articles() == null
                    || newsResponse.articles().isEmpty()) {
                logger.warn("Skipping NewsAPI general news cache update — status={}", newsResponse.status());
                return;
            }

            newsCache.setGeneralNews(cacheSerializer.convertObjectToJsonString(newsResponse));

            NewsSummaryDto summary = NewsSummaryMapper.toSummary(
                    newsResponse,
                    null,
                    NewsSummaryMapper.GENERAL_AGENT_HEADLINE_LIMIT
            );
            newsCache.setGeneralNewsSummary(cacheSerializer.convertObjectToJsonString(summary));
            logger.info("Successfully populated Redis layouts for NewsAPI general news.");

        } catch (Exception e) {
            logger.error("Failed to run NewsAPI general news sync or save to cache", e);
        }
    }

    private NewsResponse convertApiResponseToNewsObject(String apiResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        NewsResponse newsResponse = objectMapper.readValue(apiResponse, NewsResponse.class);
        return newsResponse;
    }
}
