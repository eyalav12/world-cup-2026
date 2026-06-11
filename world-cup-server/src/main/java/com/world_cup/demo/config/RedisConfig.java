//package com.world_cup.demo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import tools.jackson.databind.ObjectMapper;
//
//@Configuration
//public class RedisConfig {
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        return new LettuceConnectionFactory();
//    }
//
//    @Bean
//    public RedisTemplate<String,Object> redisTemplate(ObjectMapper objectMapper){
////        RedisTemplate<String,Object> template = new RedisTemplate<>();
////        template.setConnectionFactory(redisConnectionFactory());
////        template.setKeySerializer(new StringRedisSerializer());
////        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);
////        template.setValueSerializer(jsonSerializer);
////
////        template.afterPropertiesSet();
////        return template;
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//
//        // Use the injected context connection factory cleanly
//        template.setConnectionFactory(redisConnectionFactory());
//
//        // Key Serializers
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // Value Serializers (Powered by your exact ObjectMapper bean)
//        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);
//        template.setValueSerializer(jsonSerializer);
//        template.setHashValueSerializer(jsonSerializer);
//
//        template.afterPropertiesSet();
//        return template;
//    }
//
//}



package com.world_cup.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Standard string keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use Spring Data's native, type-preserving JSON serializer
        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}