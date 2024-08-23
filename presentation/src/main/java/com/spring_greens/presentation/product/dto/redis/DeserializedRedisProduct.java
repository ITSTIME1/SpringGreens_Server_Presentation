package com.spring_greens.presentation.product.dto.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeserializedRedisProduct extends RedisProduct<DeserializedRedisShopInformation> {}
