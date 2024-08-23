package com.spring_greens.presentation.global.socket.controller;

import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.global.socket.dto.DisconnectChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/socket")
public class SocketController {
    private final RedisTemplate<String, String> redisTemplate;

    public SocketController(@Lazy RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @PostMapping("/disconnect")
    public void disconnect(@RequestBody DisconnectChannel disconnectChannel, @AuthenticationPrincipal CustomUser customUser) {
        log.info("Socket disconnect");
        log.info(disconnectChannel.getChannel());
        List<String> userIds = redisTemplate.opsForList().range(disconnectChannel.getChannel()+"Channel", 0, -1);
        assert userIds != null;
        log.info(String.valueOf(customUser.getId()));
        if(userIds.contains(String.valueOf(customUser.getId()))) {
            log.info("Exists");
            redisTemplate.opsForList().remove(disconnectChannel.getChannel()+"Channel", 1, String.valueOf(customUser.getId()));
            log.info("Success delete");
        }
    }
}
