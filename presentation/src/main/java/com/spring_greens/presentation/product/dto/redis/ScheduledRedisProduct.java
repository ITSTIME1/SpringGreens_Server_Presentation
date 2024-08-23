package com.spring_greens.presentation.product.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spring_greens.presentation.shop.dto.information.ScheduledRedisShopInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduledRedisProduct extends RedisProduct<ScheduledRedisShopInformation> {}
