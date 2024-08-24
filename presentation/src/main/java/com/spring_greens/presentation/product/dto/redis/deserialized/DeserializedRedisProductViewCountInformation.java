package com.spring_greens.presentation.product.dto.redis.deserialized;

import com.spring_greens.presentation.product.dto.redis.information.ProductViewCountInformation;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
public class DeserializedRedisProductViewCountInformation extends ProductViewCountInformation { }
