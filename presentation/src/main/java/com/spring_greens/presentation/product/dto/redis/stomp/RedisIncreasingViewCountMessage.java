package com.spring_greens.presentation.product.dto.redis.stomp;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class RedisIncreasingViewCountMessage  {
    private Long newViewCount;
}
