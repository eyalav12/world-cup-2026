package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.MatchDto;
import com.world_cup.demo.entities.Match;
import com.world_cup.demo.mapper.MatchMapper;
import com.world_cup.demo.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MatchesCache {
    private static final Logger logger = LoggerFactory.getLogger(MatchesCache.class);
    private static final int RECENT_FINISHED_MAX = 20;

    private final RedisTemplate<String, Object> redisTemplate;
    private final DateUtil dateUtil;
    private final String MATCHES_KEY = "matches";
    private final String FINISHED_GAME_KEY = "matches_finished";
    private final String RECENT_FINISHED_KEY = "matches:recent_finished";

    public MatchesCache(RedisTemplate<String, Object> redisTemplate, DateUtil dateUtil) {
        this.redisTemplate = redisTemplate;
        this.dateUtil = dateUtil;
    }

    public void putMatchesByDate(List<Match> matches) {
        try {
            Map<String, List<Match>> matchesByDate = matches.stream().collect(Collectors.groupingBy(
                    match -> String.valueOf(dateUtil.parseInstantStringToLocalDate(match.getMatchDate()))
            ));
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, List<Match>> entry : matchesByDate.entrySet()) {
                String matchesListJson = objectMapper.writeValueAsString(entry.getValue());
                redisTemplate.opsForValue().set(generateKeyMatches(entry.getKey()), matchesListJson);
            }
        } catch (Exception e) {
            logger.error("error while saving matches to redis", e);
        }
    }

    /** Refresh per-date keys from synced API/DB rows (scheduler after update). */
    public void refreshFromSyncedMatches(List<MatchDto> matches) {
        if (matches == null || matches.isEmpty()) {
            return;
        }
        List<Match> entities = matches.stream().map(MatchMapper::toEntity).toList();
        putMatchesByDate(entities);
    }

    public List<MatchDto> getCachedMatchesByDate(LocalDate date) {
        String matchesJsonString = (String) redisTemplate.opsForValue().get(generateKeyMatches(date.toString()));
        if (matchesJsonString == null) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<Match> matchesByDate = objectMapper.readValue(matchesJsonString, new TypeReference<List<Match>>() {});
        return matchesByDate.stream().map(MatchMapper::toDto).toList();
    }

    public boolean getIsMatchFinished(Integer gameId) {
        try {
            Boolean isFinished = redisTemplate.opsForSet().isMember(FINISHED_GAME_KEY, gameId);
            return Boolean.TRUE.equals(isFinished);
        } catch (Exception e) {
            logger.error("failed to fetch from redis game with id {} ", gameId, e);
            return false;
        }
    }

    public void setFinishedGameById(Integer gameId) {
        try {
            redisTemplate.opsForSet().add(FINISHED_GAME_KEY, gameId);
        } catch (Exception e) {
            logger.error("failed to save to redis game with id {} ", gameId, e);
        }
    }

    /** Prepend a newly finished match to the hot recent-results list. */
    public void prependRecentFinished(MatchDto match) {
        if (match == null || match.getMatchId() == null) {
            return;
        }
        try {
            List<MatchDto> current = getRecentFinishedFromCache(RECENT_FINISHED_MAX);
            Map<Integer, MatchDto> deduped = new LinkedHashMap<>();
            deduped.put(match.getMatchId(), match);
            for (MatchDto existing : current) {
                if (existing.getMatchId() != null) {
                    deduped.putIfAbsent(existing.getMatchId(), existing);
                }
            }
            List<MatchDto> updated = new ArrayList<>(deduped.values());
            if (updated.size() > RECENT_FINISHED_MAX) {
                updated = updated.subList(0, RECENT_FINISHED_MAX);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            redisTemplate.opsForValue().set(
                    RECENT_FINISHED_KEY,
                    objectMapper.writeValueAsString(updated),
                    7,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            logger.error("failed to update recent finished matches in redis", e);
        }
    }

    public List<MatchDto> getRecentFinished(int limit) {
        List<MatchDto> cached = getRecentFinishedFromCache(limit);
        if (cached != null && !cached.isEmpty()) {
            return cached.size() > limit ? cached.subList(0, limit) : cached;
        }
        return null;
    }

    public void seedRecentFinished(List<MatchDto> matches) {
        if (matches == null || matches.isEmpty()) {
            return;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            redisTemplate.opsForValue().set(
                    RECENT_FINISHED_KEY,
                    objectMapper.writeValueAsString(matches),
                    7,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            logger.error("failed to seed recent finished matches in redis", e);
        }
    }

    private List<MatchDto> getRecentFinishedFromCache(int limit) {
        try {
            String json = (String) redisTemplate.opsForValue().get(RECENT_FINISHED_KEY);
            if (json == null) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<MatchDto> list = objectMapper.readValue(json, new TypeReference<List<MatchDto>>() {});
            if (list == null) {
                return null;
            }
            return list.size() > limit ? list.subList(0, limit) : list;
        } catch (Exception e) {
            logger.error("failed to read recent finished matches from redis", e);
            return null;
        }
    }

    public String generateKeyMatches(String date) {
        return MATCHES_KEY + ":" + date;
    }
}
