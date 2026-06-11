////package com.world_cup.demo.cache;
////
////import org.springframework.data.redis.core.RedisTemplate;
////import org.springframework.stereotype.Service;
////
////import java.util.concurrent.TimeUnit;
////
////@Service
////public class RedisService {
////
////    private final RedisTemplate<String,Object> redisTemplate;
////
////    public RedisService(RedisTemplate<String,Object> redisTemplate){
////        this.redisTemplate = redisTemplate;
////    }
////
////    public void set(String key, Object value, long timeout, TimeUnit unit){
////        redisTemplate.opsForValue().set(key,value,timeout,unit);
////    }
////
////    @SuppressWarnings("unchecked")
////    public <T> T get(String key,Class<T> targetClass){
////        Object value = redisTemplate.opsForValue().get(key);
////        return value!=null ? targetClass.cast(value):null;
////    }
////}
//
//package com.world_cup.demo.cache;
//
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import tools.jackson.databind.ObjectMapper;
//
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class RedisService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper; // 2. Add the Jackson ObjectMapper
//
//    // 3. Inject it into the constructor
//    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper){
//        this.redisTemplate = redisTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    public void set(String key, Object value, long timeout, TimeUnit unit){
//        redisTemplate.opsForValue().set(key, value, timeout, unit);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> T get(String key, Class<T> targetClass){
//        Object value = redisTemplate.opsForValue().get(key);
//        if (value == null) return null;
//
//        // 4. If it's already the requested type, return it
//        if (targetClass.isInstance(value)) {
//            return targetClass.cast(value);
//        }
//
//        // 5. FIX HERE: If it's a String text block from Redis, use Jackson to map it to your DTO object
//        try {
//            return objectMapper.convertValue(value, targetClass);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
//




package com.world_cup.demo.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // No ObjectMapper needed here anymore! Spring handles it behind the scenes.
    public RedisService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, Object value, long timeout, TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public <T> T get(String key, Class<T> targetClass){
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;

        // Pure, clean cast. The configured template ensures the object
        // arriving here is already instantiated into its true class type.
        return targetClass.cast(value);
    }
}
