package com.spring_greens.presentation.global.redis.converter.ifs;

import com.fasterxml.jackson.databind.JsonNode;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.ScheduledRedisProduct;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import com.spring_greens.presentation.product.dto.redis.request.RedisProductRequest;
import com.spring_greens.presentation.product.dto.redis.response.MapRedisProductResponse;
import com.spring_greens.presentation.product.dto.redis.response.ifs.RedisProductResponse;

import java.util.List;

public interface RedisConverter{
    RedisProductResponse createResponse(String domain, DeserializedRedisProduct deserializedRedisProduct);
    RedisProductRequest createRequest(String domain, String mallName);

    MapRedisProductResponse convertMapRedisProductResponse(DeserializedRedisProduct deserializedRedisProduct);

    DeserializedRedisProduct convertDeserializedRedisProduct(JsonNode jsonNode, List<DeserializedRedisShopInformation> deserializedRedisShopInformationList);

    DeserializedRedisProductInformation convertDeserializedRedisProductInformation(JsonNode jsonNode);

    DeserializedRedisShopInformation convertDeserializedRedisShopInformation(JsonNode jsonNode, List<DeserializedRedisProductInformation> deserializedRedisProductInformationList);

    ScheduledRedisProduct convertScheduledRedisProduct(DeserializedRedisProduct deserializedRedisProduct);
}
