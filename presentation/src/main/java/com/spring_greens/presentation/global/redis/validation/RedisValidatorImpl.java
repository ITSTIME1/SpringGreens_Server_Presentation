package com.spring_greens.presentation.global.redis.validation;

import com.spring_greens.presentation.global.redis.validation.ifs.RedisValidatable;
import com.spring_greens.presentation.global.redis.validation.ifs.RedisValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisValidatorImpl implements RedisValidator {
    @Override
    public void validate(RedisValidatable object) {
        object.validate();
    }
}
