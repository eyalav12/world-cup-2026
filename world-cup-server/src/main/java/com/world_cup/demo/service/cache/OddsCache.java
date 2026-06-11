package com.world_cup.demo.service.cache;

import com.world_cup.demo.cache.RedisService;
import com.world_cup.demo.dto.OddsSummaryDTO;
import com.world_cup.demo.entities.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OddsCache {

    private static final Logger logger = LoggerFactory.getLogger(OddsCache.class);
    private RedisService redisService;
    private RedisTemplate<String,Object> redisTemplate;
    private final CacheSerializer cacheSerializer;
    private final String ODDS_KEY = "odds";

    public OddsCache(RedisService redisService,RedisTemplate<String,Object> redisTemplate,CacheSerializer cacheSerializer){
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
        this.cacheSerializer = cacheSerializer;
    }

    public void cacheOdds(String key,String oddsData) {
        try {
            redisService.set(key, oddsData,12, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.error("Failed to cache odds data for matchId: " +key , e);
        }
    }

    public OddsSummaryDTO getCacheOdds(String matchId) {
        try {
            String key = generateKeyOdds(matchId);
            String oddsForMatchesJson = (String) redisTemplate.opsForValue().get(key);
            if(oddsForMatchesJson == null){
                return null;
            }
            return cacheSerializer.convertJsonStringToObject(key,OddsSummaryDTO.class);
        } catch (Exception e) {
            logger.error("Failed to retrieve cached odds data for matchId: " + matchId, e);
        }
        return null;

    }

//    public OddsSummaryDTO getCachedOdds(String matchId) {
//        try {
//            String key = generateKeyOdds(matchId);
//            OddsSummaryDTO cachedData = redisService.get(key, OddsSummaryDTO.class);
//            if (cachedData != null) {
//                // Assuming you have a method to convert JSON string back to List<OddsSummaryDTO>
//                return cachedData;
//            }
//        } catch (Exception e) {
//            logger.error("Failed to retrieve cached odds data for matchId: " + matchId, e);
//        }
//        return null;
//    }

    public String generateKeyOdds(String matchId){
        StringBuilder matchesKey = new StringBuilder(ODDS_KEY).append(":").append("summary").append(":").append(matchId);
        return matchesKey.toString();
    }
}
