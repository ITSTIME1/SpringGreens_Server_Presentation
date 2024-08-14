/*
package com.spring_greens.presentation.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spring_greens.presentation.auth.security.Jwt.JwtAuthenticationFilter;
import com.spring_greens.presentation.auth.security.Jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //로그인 페이지 직접 생성
        http.formLogin((formLogin)-> formLogin.disable());
        //httpBasic 사용 X
        http.httpBasic((httpBasic) -> httpBasic.disable());
        //CSRF 설정 비활성화
        http.csrf((csrf) -> csrf.disable());
        //세션 사용 X
        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //인가
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                    //로그인 페이지는 모두 가능
                    .requestMatchers("/member/login").permitAll()
                    //테스트 페이지는 0,1,2 역할만 가능
                    .requestMatchers("/member/test").hasAnyRole("0","1","2")
                    //제외 페이지는 모두 권한이 있어야만 가능.
                    .anyRequest().authenticated());
        //필터 순서(jwt필터를 usernamePassword 필터 앞에 실행되도록.)(로그인 전에 JWT를 이용해 추가 로그인 확인 없이도 실행하도록 함. 컨텍스트 홀더에 그냥 넣어버림.)
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
*/
