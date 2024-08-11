package com.spring_greens.presentation.global.redis.validation.util;

public class RedisValidationUtils {
    public static void validateDomain(String domain) {
        if(domain == null) {
            throw new NullPointerException("Domain is null");
        }
        if(domain.isEmpty()) {
            throw new IllegalArgumentException("Domain is empty");
        }
    }

    public static void validateMallName(String mallName) {
        if(mallName == null) {
            throw new NullPointerException("MallName is null");
        }
        if(mallName.isEmpty()) {
            throw new IllegalArgumentException("MallName is empty");
        }
    }

}
