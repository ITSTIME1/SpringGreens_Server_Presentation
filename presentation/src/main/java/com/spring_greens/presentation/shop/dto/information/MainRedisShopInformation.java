package com.spring_greens.presentation.shop.dto.information;

import com.spring_greens.presentation.product.dto.redis.information.MainRedisProductInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class MainRedisShopInformation extends ShopInformation<MainRedisProductInformation> {}
