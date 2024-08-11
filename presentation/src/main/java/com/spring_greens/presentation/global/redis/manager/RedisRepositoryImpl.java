package com.spring_greens.presentation.global.redis.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.RedisProduct;
import com.spring_greens.presentation.global.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * TemplateManager contains RedisTemplate for Json, Hash. <br>
 * this class performs CRUD for Redis Server. <br>
 * the main feature of this class is that it transfers all exceptions to the Service Layer. <br>
 * @author itstime0809
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisJsonTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public DeserializedRedisProduct getProductsByMallName(final String mallName) throws JsonProcessingException {
        final Object serializedProducts = redisJsonTemplate.opsForValue().get(mallName);
        if(serializedProducts == null) {
            throw new NullPointerException();
        }
        return (DeserializedRedisProduct) objectMapper.readValue(serializedProducts.toString(), RedisProduct.class);
    }
}
