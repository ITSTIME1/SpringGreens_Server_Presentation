package com.spring_greens.presentation.product.dto.redis.request;

import com.spring_greens.presentation.global.redis.validation.ifs.RedisValidatable;
import com.spring_greens.presentation.global.redis.validation.util.RedisValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RedisProductRequest implements RedisValidatable {
    private String domain;
    private String mallName;

    @Override
    public void validate() {
        RedisValidationUtils.validateDomain(domain);
        RedisValidationUtils.validateMallName(mallName);
    }
}
