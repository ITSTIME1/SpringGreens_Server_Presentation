package com.spring_greens.presentation.global.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.exception.CommonException;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.redis.exception.RedisException;
import com.spring_greens.presentation.global.redis.repository.RedisRepository;
import com.spring_greens.presentation.global.redis.stream.RedisPublisher;
import com.spring_greens.presentation.global.redis.validation.ifs.RedisValidator;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import com.spring_greens.presentation.product.dto.redis.information.*;
import com.spring_greens.presentation.product.dto.redis.request.RedisProductRequest;
import com.spring_greens.presentation.product.dto.redis.response.MainRedisProductResponse;
import com.spring_greens.presentation.product.dto.redis.response.ifs.RedisProductResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisRepository redisRepository;
    private final ConverterFactory converterFactory;
    private final RedisValidator redisValidator;
    private final ObjectMapper objectMapper;
    private final RedisPublisher redisPublisher;

    /**
     * this variable is temporary variable for test.
     * @author itstime0809
     */
    private static final String IMAGE_DIR = "/images/";

    public RedisProductResponse getProductsFromRedisUsingKey(@NotNull RedisProductRequest redisProductRequest)  {
        try {
            DeserializedRedisProduct deserializedRedisProduct;
            // 1. 파라미터 검증
            validateRedisProductRequest(redisProductRequest);

            // 2. 상품 URL 가공
            deserializedRedisProduct = modifyImageUrl(getProduct(redisProductRequest.getMallName()));

            // 3. 상품 반환
            return convertToResponse(redisProductRequest.getDomain(), deserializedRedisProduct);

        } catch (NullPointerException e) {
            log.error("Argument is null : {}", e.getMessage(), e);
            throw new CommonException.CustomNullPointerException(e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : {}", e.getMessage(), e);
            throw new RedisException.RedisJsonProcessingException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException : {}", e.getMessage(), e);
            throw new RedisException.RedisIllegalArgumentException(e.getMessage());
        }
    }

    private RedisProductResponse convertToResponse(String domain, DeserializedRedisProduct redisProductJsonDeserializer) {
        return converterFactory.getRedisConverter().createResponse(domain, redisProductJsonDeserializer);
    }

    private DeserializedRedisProduct getProduct(String mallName) throws JsonProcessingException {
        return redisRepository.getProductsByMallName(mallName);
    }

    public void validateRedisProductRequest(@NotNull RedisProductRequest redisProductRequest) throws IllegalArgumentException{
        redisValidator.validate(redisProductRequest);
    }

    private DeserializedRedisProduct modifyImageUrl(DeserializedRedisProduct deserializedRedisProduct) {
        List<DeserializedRedisShopInformation> updatedShopList = deserializedRedisProduct.getShop_list().stream()
                .map(shopInformation -> {
                    List<DeserializedRedisProductInformation> updatedProductList = shopInformation.getProduct().stream()
                            .map(productInformation -> {
                                String updatedImageUrl = IMAGE_DIR.concat(deserializedRedisProduct.getMall_name())
                                        .concat("/")
                                        .concat(productInformation.getProduct_image_url());
                                return productInformation.toBuilder().product_image_url(updatedImageUrl).build();
                            })
                            .collect(Collectors.toList());
                    return shopInformation.toBuilder().product(updatedProductList).build();
                }).collect(Collectors.toList());
        return deserializedRedisProduct.toBuilder().shop_list(updatedShopList).build();
    }

    public boolean setScheduledRedisProduct(DeserializedRedisProduct deserializedRedisProduct) {
        /* DeserializedRedisProduct -> ScheduledRedisProduct */
        String mall_name = deserializedRedisProduct.getMall_name();
        try {
            // 여기서 상품을 저장하고 ttl을 설정하자.
            redisRepository.saveProductsByMallName(mall_name, deserializedRedisProduct);
            return true;
        } catch (NullPointerException e) {
            log.error("Argument is null : {}", e.getMessage(), e);
            throw new CommonException.CustomNullPointerException(e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : {}", e.getMessage(), e);
            throw new RedisException.RedisJsonProcessingException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException : {}", e.getMessage(), e);
            throw new RedisException.RedisIllegalArgumentException(e.getMessage());
        }
    }

    public MainRedisProductResponse getScheduledRedisProduct(String mallName) {
        try {
            DeserializedRedisProduct deserializedRedisProduct = getProduct(mallName);
            String redisViewKey = String.format("product_view_count:%s", mallName);
            Map<String, Long> viewCountMap = redisRepository.getAllProductViewCount(redisViewKey);

            List<DeserializedRedisShopInformation> updatedShopList = deserializedRedisProduct.getShop_list().stream()
                    .map(shopInformation -> updateViewCountFromShop(shopInformation, viewCountMap))
                    .collect(Collectors.toList());

            DeserializedRedisProduct updatedProduct = deserializedRedisProduct.toBuilder()
                    .shop_list(updatedShopList)
                    .build();

            return (MainRedisProductResponse) convertToResponse("main", updatedProduct);
        } catch (NullPointerException e) {
            log.error("Argument is null : {}", e.getMessage(), e);
            throw new CommonException.CustomNullPointerException(e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : {}", e.getMessage(), e);
            throw new RedisException.RedisJsonProcessingException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException : {}", e.getMessage(), e);
            throw new RedisException.RedisIllegalArgumentException(e.getMessage());
        }
    }

    private DeserializedRedisShopInformation updateViewCountFromShop(DeserializedRedisShopInformation shopInformation, Map<String, Long> viewCountMap) {
        List<DeserializedRedisProductInformation> updatedProductList = shopInformation.getProduct().stream()
                .map(productInformation -> updateViewCountFromProduct(productInformation, viewCountMap))
                .collect(Collectors.toList());

        return shopInformation.toBuilder()
                .product(updatedProductList)
                .build();
    }

    private DeserializedRedisProductInformation updateViewCountFromProduct(DeserializedRedisProductInformation productInformation, Map<String, Long> viewCountMap) {
        Long viewCount = viewCountMap.get(productInformation.getProduct_id().toString());
        Integer intViewCount = (viewCount != null) ? viewCount.intValue() : 0;

        return productInformation.toBuilder()
                .product_view_count(intViewCount)
                .build();
    }

     public boolean setIncrementRedisProductViewCount(String mallName, long productId) {
        try {
            String redisViewKey = String.format("product_view_count:%s", mallName);

            Integer incrementedViewCount = redisRepository.incrementProductViewCount(redisViewKey,productId);
            UpdateProductViewCountInformation updateProductViewInformation = UpdateProductViewCountInformation.builder()
                .mall_name(mallName)
                .product_id(productId)
                .view_count(incrementedViewCount)
                .build();

            String serializedProductView =  objectMapper.writeValueAsString(updateProductViewInformation);
            String channel = String.format("/%s", mallName);
            redisPublisher.publishMessage(channel, serializedProductView);
            return true;
        } catch (NullPointerException e) {
            log.error("Argument is null : {}", e.getMessage(), e);
            throw new CommonException.CustomNullPointerException(e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : {}", e.getMessage(), e);
            throw new RedisException.RedisJsonProcessingException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException : {}", e.getMessage(), e);
            throw new RedisException.RedisIllegalArgumentException(e.getMessage());
        }
     }

     public Long getRemainingTime(String mallName){
        Long remainingTime = redisRepository.getMallRemainingTime(mallName);
        return remainingTime;
     }
}
