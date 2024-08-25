package com.spring_greens.presentation.main.controller;

import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.controller.AbstractBaseController;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.factory.service.ifs.ServiceFactory;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.response.MainRedisProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/main")
public class MainController extends AbstractBaseController {
    public MainController(ConverterFactory converterFactory, ServiceFactory serviceFactory) {
        super(converterFactory, serviceFactory);
    }

    /**
     * Scheduling server will send redisProduct this class by using webClient.
     * if then, saveScheduledProducts will save products into redis.
     * @param deserializedRedisProduct
     * @return
     */

    @PostMapping("/set/scheduled_product")
    public ApiResponse<?> saveScheduledProducts(@RequestBody DeserializedRedisProduct deserializedRedisProduct) {
        log.info("SaveScheduledProduct processing");
        boolean result = serviceFactory.getRedisService().setScheduledRedisProduct(deserializedRedisProduct);
        if(!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    @GetMapping("/get/scheduledProduct/{mall_name}")
    public ApiResponse<MainRedisProductResponse> getScheduleProducts(@PathVariable("mall_name") String mallName) {
        MainRedisProductResponse mainRedisProductResponse = serviceFactory.getRedisService().getScheduledRedisProduct(mallName);
        return ApiResponse.ok(mainRedisProductResponse);
    }

    @PostMapping("/set/scheduledProduct/incrementViewCount/{mall_name}/{product_Id}")
    public ApiResponse<?> setIncrementProductViewCount(@PathVariable("mall_name") String mallName,  @PathVariable("product_Id") long productId) {
        if (mallName == null || mallName.isEmpty() || productId == 0) {
            return ApiResponse.fail();
        }
        boolean result = serviceFactory.getRedisService().setIncrementRedisProductViewCount(mallName, productId);
        if(!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    /**
     * Get remaining Time of Saved redisProducts that specific mall
     * @param mallName
     * @return
     */

    @GetMapping("/get/remaining_time/{mall_name}")
    public ApiResponse<?> getRemainingTimeOfScheduledRedisProduct(@PathVariable("mall_name") String mallName){
        Long result = serviceFactory.getRedisService().getRemainingTime(mallName);
        log.info("{} : {}", result / 60, result % 60);
        return ApiResponse.ok(result);
    }
}
