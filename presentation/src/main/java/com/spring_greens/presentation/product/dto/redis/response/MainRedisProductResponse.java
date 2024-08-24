package com.spring_greens.presentation.product.dto.redis.response;

import com.spring_greens.presentation.product.dto.redis.RedisProduct;
import com.spring_greens.presentation.product.dto.redis.response.ifs.RedisProductResponse;
import com.spring_greens.presentation.shop.dto.information.MainRedisShopInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class MainRedisProductResponse extends RedisProduct<MainRedisShopInformation> implements RedisProductResponse {}
