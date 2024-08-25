package com.spring_greens.presentation.global.redis.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.redis.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisSubscriber implements MessageListener {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            String topic = new String(message.getChannel());

            simpMessagingTemplate.convertAndSend(topic, payload);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException : {}", e.getMessage(), e);
            throw new RedisException.RedisIllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RedisException.RedisIOException(e.getMessage());
        }
    }
}