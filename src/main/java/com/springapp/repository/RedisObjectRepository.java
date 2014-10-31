package com.springapp.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;


public class RedisObjectRepository {

    private HashOperations<String, String, Object> hashOps;

    public RedisObjectRepository(){

    }
    public RedisObjectRepository(RedisTemplate<String, Object> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();

    }

    public HashOperations<String, String, Object> getHashOps() {
        return hashOps;
    }
}
