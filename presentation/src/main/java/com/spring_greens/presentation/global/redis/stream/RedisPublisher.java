package com.spring_greens.presentation.global.redis.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisPublisher {
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisPublisher(StringRedisTemplate redisTemplate) {
        this.stringRedisTemplate = redisTemplate;
    }

    public void publishMessage(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }
}