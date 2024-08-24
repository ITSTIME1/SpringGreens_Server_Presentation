package com.spring_greens.presentation.product.dto.redis.information;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.spring_greens.presentation.product.deserializer.redis.RedisProductViewCountInformationJsonDeserializer;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@JsonDeserialize(using = RedisProductViewCountInformationJsonDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ProductViewCountInformation {
    private String mall_name;
    private long product_id;
    private Integer view_count;
}