package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.NewsResponse;
import com.world_cup.demo.dto.NewsSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NewsCache {

    private RedisTemplate<String,Object> redisTemplate;
    private final CacheSerializer cacheSerializer;
    private final Logger logger = LoggerFactory.getLogger(NewsCache.class);

    private final String GENERAL_UI_NEWS_KEY = "news:general";
    private final String GENERAL_AGENT_NEWS_KEY = "news:general:summary";
    private final String NEWS_BY_TEAM_KEY = "news:";
    private final String NEWS_BY_TEAM_SUMMARY_KEY = "news:summary:";

    public NewsCache(RedisTemplate<String,Object> redisTemplate,CacheSerializer cacheSerializer){
        this.redisTemplate = redisTemplate;
        this.cacheSerializer = cacheSerializer;
    }

    public void setNewsForTeam(String teamName,String teamNewsJsonString){
        try{
            String key = NEWS_BY_TEAM_KEY + teamName;
            redisTemplate.opsForValue().set(key,teamNewsJsonString);
        }
        catch(Exception e){
            logger.error("failed to save news for team {} to redis cache",teamName,e);
        }
    }

    public void setNewsSummaryForTeam(String teamName, String teamNewsSummaryJsonString) {
        try {
            String key = NEWS_BY_TEAM_SUMMARY_KEY + teamName;
            redisTemplate.opsForValue().set(key, teamNewsSummaryJsonString);
        } catch (Exception e) {
            logger.error("failed to save NewsAPI agent summary for team {} to redis cache", teamName, e);
        }
    }

    public NewsResponse getNewsForTeam(String teamName){
        try{
            String key =NEWS_BY_TEAM_KEY + teamName;
            return cacheSerializer.convertJsonStringToObject(key,NewsResponse.class);
        }
        catch(Exception e){
            logger.error("failed to fetch news for team {} ",teamName,e);
            return null;
        }
    }

    public NewsSummaryDto getNewsSummaryForTeam(String teamName) {
        try {
            String key = NEWS_BY_TEAM_SUMMARY_KEY + teamName;
            return cacheSerializer.convertJsonStringToObject(key, NewsSummaryDto.class);
        } catch (Exception e) {
            logger.error("failed to fetch NewsAPI agent summary for team {}", teamName, e);
            return null;
        }
    }

    public void setGeneralNews(String generalNewsJsonString){
        try{

            redisTemplate.opsForValue().set(GENERAL_UI_NEWS_KEY,generalNewsJsonString);
        }
        catch(Exception e){
            logger.error("failed to save general news to redis cache",e);
        }
    }

    public void setGeneralNewsSummary(String generalNewsSummaryJsonString) {
        try {
            redisTemplate.opsForValue().set(GENERAL_AGENT_NEWS_KEY, generalNewsSummaryJsonString);
        } catch (Exception e) {
            logger.error("failed to save NewsAPI general agent summary to redis cache", e);
        }
    }

    public NewsResponse getGeneralNews(){
        try{
            return cacheSerializer.convertJsonStringToObject(GENERAL_UI_NEWS_KEY,NewsResponse.class);
        }
        catch(Exception e){
            logger.error("failed to get general news from cache");
            return null;
        }
    }

    public NewsSummaryDto getGeneralNewsSummary() {
        try {
            return cacheSerializer.convertJsonStringToObject(GENERAL_AGENT_NEWS_KEY, NewsSummaryDto.class);
        } catch (Exception e) {
            logger.error("failed to get NewsAPI general agent summary from cache", e);
            return null;
        }
    }

}
