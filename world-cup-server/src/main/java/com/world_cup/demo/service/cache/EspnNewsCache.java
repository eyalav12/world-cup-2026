package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.NewsSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EspnNewsCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheSerializer cacheSerializer;
    private final Logger logger = LoggerFactory.getLogger(EspnNewsCache.class);

    private static final String GENERAL_UI_NEWS_KEY = "espn:news:general";
    private static final String GENERAL_AGENT_NEWS_KEY = "espn:news:general:summary";
    private static final String NEWS_BY_TEAM_KEY = "espn:news:";
    private static final String NEWS_BY_TEAM_SUMMARY_KEY = "espn:news:summary:";

    public EspnNewsCache(RedisTemplate<String, Object> redisTemplate, CacheSerializer cacheSerializer) {
        this.redisTemplate = redisTemplate;
        this.cacheSerializer = cacheSerializer;
    }

    public void setNewsForTeam(String teamName, String teamNewsJsonString) {
        try {
            String key = NEWS_BY_TEAM_KEY + teamName;
            redisTemplate.opsForValue().set(key, teamNewsJsonString);
        } catch (Exception e) {
            logger.error("failed to save ESPN news for team {} to redis cache", teamName, e);
        }
    }

    public void setNewsSummaryForTeam(String teamName, String teamNewsSummaryJsonString) {
        try {
            String key = NEWS_BY_TEAM_SUMMARY_KEY + teamName;
            redisTemplate.opsForValue().set(key, teamNewsSummaryJsonString);
        } catch (Exception e) {
            logger.error("failed to save ESPN agent news summary for team {} to redis cache", teamName, e);
        }
    }

    public NewsResponse getNewsForTeam(String teamName) {
        try {
            String key = NEWS_BY_TEAM_KEY + teamName;
            return cacheSerializer.convertJsonStringToObject(key, NewsResponse.class);
        } catch (Exception e) {
            logger.error("failed to fetch ESPN news for team {}", teamName, e);
            return null;
        }
    }

    public NewsSummaryDto getNewsSummaryForTeam(String teamName) {
        try {
            String key = NEWS_BY_TEAM_SUMMARY_KEY + teamName;
            return cacheSerializer.convertJsonStringToObject(key, NewsSummaryDto.class);
        } catch (Exception e) {
            logger.error("failed to fetch ESPN agent news summary for team {}", teamName, e);
            return null;
        }
    }

    public void setGeneralNews(String generalNewsJsonString) {
        try {
            redisTemplate.opsForValue().set(GENERAL_UI_NEWS_KEY, generalNewsJsonString);
        } catch (Exception e) {
            logger.error("failed to save ESPN general news to redis cache", e);
        }
    }

    public void setGeneralNewsSummary(String generalNewsSummaryJsonString) {
        try {
            redisTemplate.opsForValue().set(GENERAL_AGENT_NEWS_KEY, generalNewsSummaryJsonString);
        } catch (Exception e) {
            logger.error("failed to save ESPN general agent news summary to redis cache", e);
        }
    }

    public NewsResponse getGeneralNews() {
        try {
            return cacheSerializer.convertJsonStringToObject(GENERAL_UI_NEWS_KEY, NewsResponse.class);
        } catch (Exception e) {
            logger.error("failed to get ESPN general news from cache", e);
            return null;
        }
    }

    public NewsSummaryDto getGeneralNewsSummary() {
        try {
            return cacheSerializer.convertJsonStringToObject(GENERAL_AGENT_NEWS_KEY, NewsSummaryDto.class);
        } catch (Exception e) {
            logger.error("failed to get ESPN general agent news summary from cache", e);
            return null;
        }
    }
}
