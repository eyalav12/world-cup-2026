package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.MatchLineupSummaryDto;
import com.world_cup.demo.dto.MatchLineupsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LineupCache {

    private static final String LINEUP_KEY_PREFIX = "lineup:match:";
    private static final String LINEUP_SUMMARY_KEY_PREFIX = "lineup:summary:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheSerializer cacheSerializer;
    private final Logger logger = LoggerFactory.getLogger(LineupCache.class);

    public LineupCache(RedisTemplate<String, Object> redisTemplate, CacheSerializer cacheSerializer) {
        this.redisTemplate = redisTemplate;
        this.cacheSerializer = cacheSerializer;
    }

    public void setLineups(Integer matchId, String lineupsJson) {
        try {
            redisTemplate.opsForValue().set(LINEUP_KEY_PREFIX + matchId, lineupsJson);
        } catch (Exception e) {
            logger.error("failed to save lineups for match {} to redis", matchId, e);
        }
    }

    public void setLineupSummary(Integer matchId, String summaryJson) {
        try {
            redisTemplate.opsForValue().set(LINEUP_SUMMARY_KEY_PREFIX + matchId, summaryJson);
        } catch (Exception e) {
            logger.error("failed to save lineup summary for match {} to redis", matchId, e);
        }
    }

    public MatchLineupsDto getLineups(Integer matchId) {
        try {
            return cacheSerializer.convertJsonStringToObject(LINEUP_KEY_PREFIX + matchId, MatchLineupsDto.class);
        } catch (Exception e) {
            logger.error("failed to fetch lineups for match {} from redis", matchId, e);
            return null;
        }
    }

    public MatchLineupSummaryDto getLineupSummary(Integer matchId) {
        try {
            return cacheSerializer.convertJsonStringToObject(
                    LINEUP_SUMMARY_KEY_PREFIX + matchId,
                    MatchLineupSummaryDto.class
            );
        } catch (Exception e) {
            logger.error("failed to fetch lineup summary for match {} from redis", matchId, e);
            return null;
        }
    }
}
