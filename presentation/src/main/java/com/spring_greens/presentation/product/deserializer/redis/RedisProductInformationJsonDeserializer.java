package com.spring_greens.presentation.product.deserializer.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisProductInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;



@Slf4j
@RequiredArgsConstructor
public class RedisProductInformationJsonDeserializer extends JsonDeserializer<DeserializedRedisProductInformation> {
    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;


    @Override
    public DeserializedRedisProductInformation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonParser);
            return converterFactory.getRedisConverter().convertDeserializedRedisProductInformation(jsonNode);
        } catch (IOException e) {
            log.error("Error deserializing productInformation JSON: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
