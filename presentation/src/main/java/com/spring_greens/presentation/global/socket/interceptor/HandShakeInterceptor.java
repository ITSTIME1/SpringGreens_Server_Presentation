package com.spring_greens.presentation.global.socket.interceptor;

import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Interceptor is responsible for extract verified jwt token and save user id to redis.
 * User can connect to socket server with using stomp sub protocol. if so client send with jwt token on header.
 * stomp protocol first process handshake client between server. and then after handshake.
 * this interceptor execute for save user id to redis.
 * @author itstime0809
 */

@Slf4j
@Component
public class HandShakeInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public HandShakeInterceptor(JwtProvider jwtProvider, @Lazy RedisTemplate<String, String> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("HandShakeInterceptor execute");
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            // Authorization 헤더에서 액세스 토큰 추출
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // "Bearer " 제거
                log.info("Toke : {}", token);
                try {
                    // 액세스 토큰 검증
                    boolean isValid = jwtProvider.validToken(token, JwtProvider.Access_TOKEN_NAME);
                    if (isValid) {
                        // 유효한 토큰이면 CustomUser로부터 userId 추출

                        // 여기서 가지고 오지 못하는구나
                        Authentication authentication = jwtProvider.getAuthentication(token);
                        CustomUser customUser = (CustomUser) authentication.getPrincipal();
                        Long userId = customUser.getId();

                        log.info("UserID : {}", userId);
//                        Long userId = customUser.getId();
//                        log.info(String.valueOf(userId));
                        // 추가로 channelHeader 추출
                        String channelHeader = accessor.getFirstNativeHeader("channelHeader");
                        if (channelHeader != null) {
                            log.info("Channel header: " + channelHeader);

                            redisTemplate.opsForList().leftPush(channelHeader+"Channel", String.valueOf(userId));
                            List<String> userIds = redisTemplate.opsForList().range(channelHeader+"Channel", 0, -1);
                            assert userIds != null;
                            for(String id : userIds) {
                                log.info("redis channel id for apm {}", id);
                            }

                            log.info("Success save to redis db");
                        } else {
                            log.warn("No channelHeader found");
                            throw new RuntimeException("channelHeader missing");
                        }

                        log.info("Valid token: " + token);
                        // 토큰이 유효하면 메시지를 계속 진행
                        return message;
                    } else {
                        log.warn("Invalid token: " + token);
                        // 토큰이 유효하지 않으면 연결 거부
                        return message;
                    }
                } catch (Exception e) {
                    log.error("Token validation error: ", e);
                    // 토큰 검증 중 오류가 발생하면 연결 거부
                    return message;
                }
            } else {
                log.warn("No Authorization header found or not Bearer token");
                // Authorization 헤더가 없거나 Bearer 토큰이 아닌 경우 연결 거부
                return message;
            }
        }

        // StompHeaderAccessor가 null인 경우, 메시지를 계속 진행
        return message;
    }
}