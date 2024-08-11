package com.spring_greens.presentation.global.factory.converter.ifs;

import com.spring_greens.presentation.fcm.converter.ifs.FcmConverter;
import com.spring_greens.presentation.global.redis.converter.ifs.RedisConverter;
import com.spring_greens.presentation.map.converter.ifs.MallConverter;

public interface ConverterFactory {
    MallConverter getMallConverter();
    RedisConverter getRedisConverter();
    FcmConverter getFcmConverter();
}
