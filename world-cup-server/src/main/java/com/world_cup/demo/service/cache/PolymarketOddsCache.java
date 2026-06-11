package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.CleanedPolymarketOdds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PolymarketOddsCache {
    private final Logger logger = LoggerFactory.getLogger(PolymarketOddsCache.class);
    private final RedisTemplate<String,Object> redisTemplate;
    private final CacheSerializer cacheSerializer;
    private final String BASE_KEY = "polymarket:";

    public PolymarketOddsCache(RedisTemplate<String,Object> redisTemplate,CacheSerializer cacheSerializer){
        this.redisTemplate = redisTemplate;
        this.cacheSerializer = cacheSerializer;
    }

    public void setTopScorerOdds(String topScorerOdds){
        try{
            String key = getTopScorerOddsKey();
            redisTemplate.opsForValue().set(key,topScorerOdds);
        }
        catch(Exception e){
            logger.error("failed to save polymarket top scorer odds to cache",e);
        }
    }

    public void setGroupWinnerOdds(String groupWinnerOdds, String group){
        try{
            String key = getGroupWinnerOddsKey(group);
            redisTemplate.opsForValue().set(key,groupWinnerOdds);
        }
        catch(Exception e){
            logger.error("failed to save polymarket top scorer odds to cache",e);
        }
    }

    public CleanedPolymarketOdds getGroupWinnerOdds(String group){
        return cacheSerializer.convertJsonStringToObject(getGroupWinnerOddsKey(group),CleanedPolymarketOdds.class);
    }

    public void setAdvancementOdds(String advancementOdds, String stage) {
        try {
            String key = getAdvancementOddsKey(stage);
            redisTemplate.opsForValue().set(key, advancementOdds);
        } catch (Exception e) {
            logger.error("failed to save polymarket advancement odds to cache", e);
        }
    }

    public CleanedPolymarketOdds getAdvancementOdds(String stage) {
        try {
            return cacheSerializer.convertJsonStringToObject(getAdvancementOddsKey(stage), CleanedPolymarketOdds.class);
        } catch (Exception e) {
            logger.error("failed to fetch polymarket advancement odds from cache", e);
            return null;
        }
    }

    public CleanedPolymarketOdds getTopScorerOdds(){
        return cacheSerializer.convertJsonStringToObject(getTopScorerOddsKey(),CleanedPolymarketOdds.class);
    }

    public void setWinnerOdds(String winnerOddsJsonString){
        try{
            String key = getWinnerOddsKey();
            redisTemplate.opsForValue().set(key,winnerOddsJsonString);
        }
        catch(Exception e){
            logger.error("error to save winner polymarket odds to cache",e);
        }
    }

    public CleanedPolymarketOdds getWinnerOdds(){
        try{
            return cacheSerializer.convertJsonStringToObject(getWinnerOddsKey(), CleanedPolymarketOdds.class);
        }
        catch (Exception e){
            logger.error("error to fetch from cache teams winner odds",e);
            return null;
        }
    }

    private String getWinnerOddsKey(){
        return BASE_KEY+"winner";
    }

    private String getTopScorerOddsKey(){
        return BASE_KEY+"topscorer";
    }

    private String getGroupWinnerOddsKey(String group){
        return BASE_KEY+"groupwinner:" + group;
    }

    private String getAdvancementOddsKey(String stage) {
        return BASE_KEY + "advancement:" + stage;
    }
}
