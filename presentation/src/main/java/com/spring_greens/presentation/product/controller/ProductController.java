package com.spring_greens.presentation.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.redis.pub.RedisMessagePublisher;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.ScheduledRedisProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;

    private final RedisMessagePublisher redisMessagePublisher;

    public ProductController(ObjectMapper objectMapper, ConverterFactory converterFactory, RedisMessagePublisher redisMessagePublisher) {
        this.objectMapper = objectMapper;
        this.converterFactory = converterFactory;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    @PostMapping("/get/scheduled_redis_product")
    public void testGetWebClientProduct(@RequestBody DeserializedRedisProduct scheduledRedisProduct){

        // ScheduledRedisProduct로 변환. RedisProduct를 상속받게 되면, 역직렬화 된 것을 반환하기 때문에s
        // 역직렬화 된 것을 받고, redis 저장에 필요한 값만 매핑할 수 있게 convert를 수행함.
        ScheduledRedisProduct scheduledRedisProduct1 = converterFactory.getRedisConverter().convertScheduledRedisProduct(scheduledRedisProduct);

        log.info("Success getting product");
        try {
            log.info(objectMapper.writeValueAsString(scheduledRedisProduct1));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // 이제 스케줄링에서 잘 받는지 확인해보자.
    }


    // Get stomp protocol message
    // user can increase product view count. by using stomp protocol
    // channel_id is mall_name and product_id is clicked product id.
    @MessageMapping("/ws/message/increase/product/view_count/{channel_id}/{product_id}")
    public void increaseProductViewCount(@DestinationVariable("channel_id") String channelId,
                                         @DestinationVariable("product_id") String productId,
                                         @Payload String message) {
        log.info(channelId);
        log.info(productId);
        log.info(message);
//        redisMessagePublisher.increaseProductViewCountAndMakeStompMessage(channel_id, product_id);
    }
}
