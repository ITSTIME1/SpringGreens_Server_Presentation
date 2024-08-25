package com.spring_greens.presentation.global.redis.pub;

import com.spring_greens.presentation.product.dto.redis.stomp.RedisIncreasingViewCountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisMessagePublisher is responsible for making stomp message to send to subscribed user.
 * so, if publisher finally completed stomp message, delegate to 'RedisMessageSubscriber'.
 * @author itsitme0809
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final RedisTemplate<String, Long> redisHashTemplate;
    private final RedisTemplate<String, String> redisStringTemplate;

    // redis product increase view count method

    // redis key structure defined below.
    // key : mallName - hashName - primitive type is String
    // field : productId - hashField - primitive type is Long
    // value : integer - value - primitive is int
    public void increaseProductViewCountAndMakeStompMessage(String channelId, long productId) {
        log.info("Increasing redis product view count for product ID: {}", productId);
        HashOperations<String, Long, Long> hashOps = redisHashTemplate.opsForHash();
        Long newViewCount = hashOps.increment(channelId, productId, 1);
        RedisIncreasingViewCountMessage redisIncreasingViewCountMessage = RedisIncreasingViewCountMessage.builder()
                .newViewCount(newViewCount)
                .build();
        redisStringTemplate.convertAndSend(channelId, redisIncreasingViewCountMessage);
    }
}
