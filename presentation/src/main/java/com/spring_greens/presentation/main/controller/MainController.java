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

    @PostMapping("/set/scheduledProduct")
    public ApiResponse<?> setScheduleProducts(@RequestBody DeserializedRedisProduct deserializedRedisProduct) {
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
}
