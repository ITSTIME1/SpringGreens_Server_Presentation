package com.spring_greens.presentation.global.redis.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.spring_greens.presentation.global.enums.Domain;
import com.spring_greens.presentation.global.redis.converter.ifs.RedisConverter;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.ScheduledRedisProduct;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import com.spring_greens.presentation.product.dto.redis.information.MapRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.information.ScheduledRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.request.RedisProductRequest;
import com.spring_greens.presentation.product.dto.redis.response.MapRedisProductResponse;
import com.spring_greens.presentation.product.dto.redis.response.ifs.RedisProductResponse;
import com.spring_greens.presentation.shop.dto.information.MapRedisShopInformation;
import com.spring_greens.presentation.shop.dto.information.ScheduledRedisShopInformation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;



@Component
public class RedisConverterImpl implements RedisConverter {
    @Override
    public RedisProductResponse createResponse(String domain, DeserializedRedisProduct deserializedRedisProduct) {
        if(domain.equals(Domain.MAP.getDomain())) {
            return this.convertMapRedisProductResponse(deserializedRedisProduct);
        }
        return null;
    }

    @Override
    public RedisProductRequest createRequest(String domain, String mallName) {
        return RedisProductRequest.builder().domain(domain).mallName(mallName).build();
    }



    @Override
    public MapRedisProductResponse convertMapRedisProductResponse(DeserializedRedisProduct deserializedRedisProduct) {
        List<MapRedisShopInformation> mapRedisShopInformationList =
                deserializedRedisProduct.getShop_list().stream().map(shopInfo ->
                        MapRedisShopInformation.builder()
                                .shop_name(shopInfo.getShop_name())
                                .shop_contact(shopInfo.getShop_contact())
                                .shop_address_details(shopInfo.getShop_address_details())
                                .product(shopInfo.getProduct().stream().map(productInfo ->
                                                MapRedisProductInformation.builder()
                                                        .product_name(productInfo.getProduct_name())
                                                        .product_view_count(productInfo.getProduct_view_count())
                                                        .product_image_url(productInfo.getProduct_image_url())
                                                        .build())
                                        .collect(Collectors.toList()))
                                .build()).collect(Collectors.toList());
        return MapRedisProductResponse.builder()
                .mall_name(deserializedRedisProduct.getMall_name())
                .shop_list(mapRedisShopInformationList).build();
    }

    @Override
    public DeserializedRedisProduct convertDeserializedRedisProduct(JsonNode jsonNode,
                                                                    List<DeserializedRedisShopInformation> deserializedRedisShopInformationList) {
        return DeserializedRedisProduct.builder()
                .mall_id(jsonNode.has("mall_id") ? jsonNode.get("mall_id").asLong() : null)
                .mall_name(jsonNode.has("mall_name") ? jsonNode.get("mall_name").asText() : "")
                .shop_list(deserializedRedisShopInformationList)
                .build();
    }

    @Override
    public DeserializedRedisProductInformation convertDeserializedRedisProductInformation(JsonNode jsonNode) {
        return DeserializedRedisProductInformation
                .builder()
                .product_id(jsonNode.has("product_id") ? jsonNode.get("product_id").asLong() : null)
                .product_name(jsonNode.has("product_name") ? jsonNode.get("product_name").asText() : "")
                .product_price(jsonNode.has("product_price") ? jsonNode.get("product_price").asInt() : 0)
                .product_unit(jsonNode.has("product_unit") ? jsonNode.get("product_unit").asText() : "")
                .product_image_url(jsonNode.has("product_image_url") ? jsonNode.get("product_image_url").asText() : "")
                .product_view_count(jsonNode.has("product_view_count") ? jsonNode.get("product_view_count").asInt() : 0)
                .major_category(jsonNode.has("major_category") ? jsonNode.get("major_category").asText() : "")
                .sub_category(jsonNode.has("sub_category") ? jsonNode.get("sub_category").asText() : "")
                .build();
    }

    @Override
    public DeserializedRedisShopInformation convertDeserializedRedisShopInformation(JsonNode jsonNode, List<DeserializedRedisProductInformation> deserializedRedisProductInformationList) {
        return DeserializedRedisShopInformation
                .builder()
                .shop_id(jsonNode.has("shop_id") ? jsonNode.get("shop_id").asLong() : null)
                .shop_name(jsonNode.has("shop_name") ? jsonNode.get("shop_name").asText() : "")
                .shop_contact(jsonNode.has("shop_contact") ? jsonNode.get("shop_contact").asText() : "")
                .shop_address_details(jsonNode.has("shop_address_details") ? jsonNode.get("shop_address_details").asText() : "")
                .product(deserializedRedisProductInformationList)
                .build();
    }

    @Override
    public ScheduledRedisProduct convertScheduledRedisProduct(DeserializedRedisProduct deserializedRedisProduct) {
        // DeserializedRedisProduct의 필드를 ScheduledRedisProduct에 맞게 변환
        List<ScheduledRedisShopInformation> shopList = deserializedRedisProduct.getShop_list().stream()
                .map(shopInfo -> {
                    // DeserializedRedisShopInformation을 ScheduledRedisShopInformation으로 변환
                    List<ScheduledRedisProductInformation> productList = shopInfo.getProduct().stream()
                            .map(productInfo -> ScheduledRedisProductInformation.builder()
                                    .product_id(productInfo.getProduct_id())
                                    .product_name(productInfo.getProduct_name())
                                    .product_price(productInfo.getProduct_price())
                                    .product_unit(productInfo.getProduct_unit())
                                    .product_image_url(productInfo.getProduct_image_url())
                                    .product_view_count(productInfo.getProduct_view_count())
                                    .major_category(productInfo.getMajor_category())
                                    .sub_category(productInfo.getSub_category())
                                    .build())
                            .collect(Collectors.toList());

                    return ScheduledRedisShopInformation.builder()
                            .shop_id(shopInfo.getShop_id())
                            .shop_name(shopInfo.getShop_name())
                            .shop_contact(shopInfo.getShop_contact())
                            .shop_address_details(shopInfo.getShop_address_details())
                            .product(productList)
                            .build();
                })
                .collect(Collectors.toList());

        return ScheduledRedisProduct.builder()
                .mall_id(deserializedRedisProduct.getMall_id())
                .mall_name(deserializedRedisProduct.getMall_name())
                .shop_list(shopList)
                .build();
    }


}
