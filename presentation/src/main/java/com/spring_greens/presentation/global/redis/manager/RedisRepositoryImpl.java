package com.spring_greens.presentation.global.redis.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.RedisProduct;
import com.spring_greens.presentation.global.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, Long> redisHashTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public DeserializedRedisProduct getProductsByMallName(final String mallName) throws JsonProcessingException {
        final Object serializedProducts = redisJsonTemplate.opsForValue().get(mallName);
        if(serializedProducts == null) {
            throw new NullPointerException();
        }

        return (DeserializedRedisProduct) objectMapper.readValue(serializedProducts.toString(), RedisProduct.class);
    }

    public void saveProductsByMallName(final String mallName, final DeserializedRedisProduct deserializedRedisProduct) throws JsonProcessingException {
        final Object serializedRedisProduct =  objectMapper.writeValueAsString(deserializedRedisProduct);
        if(serializedRedisProduct == null) {
            throw new NullPointerException();
        }
        ValueOperations<String, Object> valueOps = redisJsonTemplate.opsForValue();
        valueOps.set(mallName, serializedRedisProduct, 300, TimeUnit.SECONDS); // 초 단위로 저장. 이후, 클라이언트에서는 초를 변환하는 작업만 하면됨.

    }

    public Map<String, Long> getAllProductViewCount(final String redisViewKey) {
        HashOperations<String, String, Long> hashOps = redisHashTemplate.opsForHash();

        return hashOps.entries(redisViewKey);
    }

    public Integer incrementProductViewCount (final String redisViewKey, final long productId) {
        HashOperations<String, String, Integer> hashOps = redisHashTemplate.opsForHash();
        return hashOps.increment(redisViewKey, Long.toString(productId), 1).intValue();
    }

    @Override
    public Long getMallRemainingTime(String mallName) {
        return redisJsonTemplate.getExpire(mallName, TimeUnit.SECONDS);
    }
}
