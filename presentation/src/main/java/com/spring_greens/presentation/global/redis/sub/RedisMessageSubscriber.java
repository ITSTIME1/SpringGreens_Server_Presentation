package com.spring_greens.presentation.global.redis.sub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info(Arrays.toString(message.getChannel()));
        log.info(Arrays.toString(message.getBody()));
//
//        log.info("Received message: {} from channel: {}", messageBody, channel);
//        messagingTemplate.convertAndSend(channel, messageBody)
    }
}
