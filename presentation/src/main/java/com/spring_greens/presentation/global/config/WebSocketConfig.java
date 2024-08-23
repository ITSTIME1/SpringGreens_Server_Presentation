package com.spring_greens.presentation.global.config;

import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.global.socket.interceptor.HandShakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker // enable websocket message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public WebSocketConfig(JwtProvider jwtProvider, @Lazy RedisTemplate<String, String> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // setting stomp endpoint
        registry.addEndpoint("/ws")
                .setAllowedOrigins("https://spring-greens-client.vercel.app", "http://localhost:3000")
                .withSockJS(); // support sock js
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // user must use below prefix when user try to increase product view count.
        config.setApplicationDestinationPrefixes("/ws/message");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new HandShakeInterceptor(jwtProvider, redisTemplate));
    }


}
