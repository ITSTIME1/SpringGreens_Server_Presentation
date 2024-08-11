package com.spring_greens.presentation.product.deserializer.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.redis.exception.RedisException;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisProductInformation;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@RequiredArgsConstructor
public class RedisShopInformationJsonDeserializer extends JsonDeserializer<DeserializedRedisShopInformation> {
    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;

    @Override
    public DeserializedRedisShopInformation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonParser);
            List<DeserializedRedisProductInformation> productInfoList = StreamSupport.stream(jsonNode.get("product").spliterator(), false)
                    .map(productNode -> {
                        try {
                            return objectMapper.readValue(productNode.traverse(), DeserializedRedisProductInformation.class);
                        } catch (IOException e) {
                            log.error("Error deserializing productNode: {}", e.getMessage(), e);
                            throw new RedisException.RedisIOException(e.getMessage());
                        }
                    })
                    .toList();

            return converterFactory.getRedisConverter().convertDeserializedRedisShopInformation(jsonNode, productInfoList);
        } catch (IOException e) {
            log.error("Error deserializing shopInformation JSON: {}", e.getMessage(), e);
            throw new RedisException.RedisIOException(e.getMessage());
        }
    }
}
