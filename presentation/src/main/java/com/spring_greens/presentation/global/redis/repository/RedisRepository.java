package com.spring_greens.presentation.global.redis.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;

public interface RedisRepository {
    DeserializedRedisProduct getProductsByMallName(final String mallName) throws JsonProcessingException;
}
