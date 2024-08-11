package com.spring_greens.presentation.global.factory.service.ifs;

import com.spring_greens.presentation.fcm.service.FcmService;
import com.spring_greens.presentation.global.redis.service.RedisService;
import com.spring_greens.presentation.mall.service.ifs.MallService;
import com.spring_greens.presentation.shop.service.ShopService;

public interface ServiceFactory {
    RedisService getRedisService();

    MallService getMallService();

    FcmService getFcmService();

    ShopService getShopService();
}
