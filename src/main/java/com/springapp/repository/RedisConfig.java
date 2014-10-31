package com.springapp.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

//    @Bean
//    public RedisObjectRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
//        return new RedisObjectRepository(redisTemplate);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Object> objSerializer = new JacksonJsonRedisSerializer<Object>(Object.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(objSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(objSerializer);

        return template;
    }

}
