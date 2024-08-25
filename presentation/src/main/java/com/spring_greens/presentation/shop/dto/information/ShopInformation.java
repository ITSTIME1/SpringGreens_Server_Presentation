package com.spring_greens.presentation.shop.dto.information;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.spring_greens.presentation.product.deserializer.redis.RedisShopInformationJsonDeserializer;
import com.spring_greens.presentation.product.dto.redis.information.ProductInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@SuperBuilder(toBuilder = true)
@Getter
@JsonDeserialize(using = RedisShopInformationJsonDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ShopInformation<T extends ProductInformation> {
    private Long shop_id;
    private String shop_name;
    private String shop_contact;
    private String shop_address_details;
    private List<T> product;
}
