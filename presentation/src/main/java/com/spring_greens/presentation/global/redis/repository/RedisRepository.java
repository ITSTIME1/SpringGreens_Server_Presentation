package com.spring_greens.presentation.global.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import java.util.Map;

public interface RedisRepository {
    DeserializedRedisProduct getProductsByMallName(final String mallName) throws JsonProcessingException;

    void saveProductsByMallName(final String mallName, final DeserializedRedisProduct deserializedRedisProduct) throws JsonProcessingException;

    Integer incrementProductViewCount (final String redisViewKey, final long productId);

    Map<String, Long> getAllProductViewCount(final String redisViewKey);

    Long getMallRemainingTime(final String mallName);
}
