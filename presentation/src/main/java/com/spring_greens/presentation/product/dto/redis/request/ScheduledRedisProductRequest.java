package com.spring_greens.presentation.product.dto.redis.request;

import com.spring_greens.presentation.global.redis.validation.ifs.RedisValidatable;
import com.spring_greens.presentation.product.dto.redis.ScheduledRedisProduct;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ScheduledRedisProductRequest extends ScheduledRedisProduct implements RedisValidatable {
    @Override
    public void validate() {

    }
}
