package com.spring_greens.presentation.product.deserializer.redis;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.redis.exception.RedisException;
import com.spring_greens.presentation.product.dto.redis.DeserializedRedisProduct;
import com.spring_greens.presentation.product.dto.redis.deserialized.DeserializedRedisShopInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;


/**
 * RedisProductJsonDeserializer is deserializer for RedisProduct when get products from redis database.<br>
 * we are using jackson library for translate JSON to POJO or POJO to JSON. <br>
 * generally, general type class well convert that what you do but generic type is not. <br>
 * RedisProduct is abstract class so that need subtype class. <br>
 * It's common class for redis product so, I used abstract class and generic. <br>
 * RedisProductJsonDeserializer will execute when @JsonDeserializer on RedisProduct perform. <br>
 * JsonDeserializer want to have default constructor, compiler creates default constructor when it's class does not have default constructor (no args). <br>
 * <br>
 * RedisProductJsonDeserializer will be used to deserialize objectMapper.<br>
 * objectMapper will be injected by spring container. <br>
 * basically, objectMapper was created by JacksonAutoConfiguration.
 *
 * @author itstime0809
 */

@Slf4j
@RequiredArgsConstructor
public class RedisProductJsonDeserializer extends JsonDeserializer<DeserializedRedisProduct> {
    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;
    @Override
    public DeserializedRedisProduct deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonParser);
            List<DeserializedRedisShopInformation> deserializedRedisShopInformationList = StreamSupport.stream(jsonNode.get("shop_list").spliterator(), false)
                    .map(shopNode -> {
                        try {

                            return objectMapper.readValue(shopNode.traverse(), DeserializedRedisShopInformation.class);
                        } catch (IOException e) {
                            log.error("Error deserializing shopNode: {}", e.getMessage(), e);
                            throw new RedisException.RedisIOException(e.getMessage());
                        }

                    })
                    .toList();


            return converterFactory.getRedisConverter().convertDeserializedRedisProduct(jsonNode, deserializedRedisShopInformationList);
        } catch (IOException e) {
            log.error("Error deserializing JSON: {}", e.getMessage(), e);
            throw new RedisException.RedisIOException(e.getMessage());
        }
    }
}

