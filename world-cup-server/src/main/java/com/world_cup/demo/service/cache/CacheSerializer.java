package com.world_cup.demo.service.cache;

import com.world_cup.demo.dto.NewsResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class CacheSerializer {
    private final RedisTemplate<String ,Object> redisTemplate;

    public CacheSerializer(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public <T> T convertJsonStringToObject(String key,Class<T> targetClass){
        String generalNews = (String) redisTemplate.opsForValue().get(key);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(generalNews, targetClass);
    }

    public <T> String convertObjectToJsonString(T objectResponse){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(objectResponse);
    }

    public <T> T parseJson(String json, Class<T> targetClass) {
        if (json == null || json.isBlank()) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, targetClass);
    }
}
