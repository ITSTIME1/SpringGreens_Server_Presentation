package com.spring_greens.presentation.global.factory.converter;

import com.spring_greens.presentation.fcm.converter.ifs.FcmConverter;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.redis.converter.ifs.RedisConverter;
import com.spring_greens.presentation.map.converter.ifs.MallConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * ConverterFactory purpose is to break dependency of controller. <br>
 * if converterFactory is not exist, AbstractBaseController has many of service bean and converter etc. <br>
 * each of controller need to initialize bean by using constructor. <br>
 * in this case, each controller increases the number of parameters to constructor. <br>
 * <br>
 * let's assume one situation, we have so many controller that contained many services. <br>
 * if controller is changed or deleted, we have to change each of controller. <br>
 *
 * In order to prevent this, I create converter factory <br>
 * as a result, this way reduces the dependence between controller and each bean. <br>
 * @author itstime0809
 */

@Component
@RequiredArgsConstructor
public class ConverterFactoryImpl implements ConverterFactory {
    @Qualifier(value = "redisConverterImpl")
    private final RedisConverter redisConverter;
    @Qualifier(value = "mallConverterImpl")
    private final MallConverter mallConverter;
    private final FcmConverter fcmConverter;

    @Override
    public MallConverter getMallConverter() {
        return mallConverter;
    }

    @Override
    public RedisConverter getRedisConverter() {
        return redisConverter;
    }

    @Override
    public FcmConverter getFcmConverter() {
        return fcmConverter;
    }
}
