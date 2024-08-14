package com.spring_greens.presentation.global.enums;

import com.spring_greens.presentation.auth.dto.oauth.GoogleResponse;
import com.spring_greens.presentation.auth.dto.oauth.KakaoResponse;
import com.spring_greens.presentation.auth.dto.oauth.NaverResponse;
import com.spring_greens.presentation.auth.dto.oauth.OAuth2Response;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public enum OAuth2ResponseEnum {
    NAVER {
        @Override
        public OAuth2Response createResponse(Map<String, Object> attributes) {
            return new NaverResponse(attributes);
        }
    },
    GOOGLE {
        @Override
        public OAuth2Response createResponse(Map<String, Object> attributes) {
            return new GoogleResponse(attributes);
        }
    },

    KAKAO {
        @Override
        public OAuth2Response createResponse(Map<String, Object> attributes) {
            return new KakaoResponse(attributes);
        }
    };

    public abstract OAuth2Response createResponse(Map<String, Object> attributes);

    public static OAuth2ResponseEnum getByRegistrationId(String registrationId) {
        Optional<OAuth2ResponseEnum> provider = Arrays.stream(values())
                .filter(p -> p.name().equalsIgnoreCase(registrationId))
                .findFirst();
        return provider.orElse(null);
    }
}