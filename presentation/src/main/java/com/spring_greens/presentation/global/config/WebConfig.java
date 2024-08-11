package com.spring_greens.presentation.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-absolute-path}")
    private String absolutePath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                // 환경변수로 만들어줘야함.
                .addResourceLocations("file:".concat(absolutePath));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://spring-greens-client.vercel.app/")  // 허용할 출처를 설정하세요
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메소드 설정
                .allowedHeaders("*")  // 허용할 헤더를 설정합니다
                .allowCredentials(true);  // 자격 증명(쿠키 등)을 허용할지 설정
    }
}
