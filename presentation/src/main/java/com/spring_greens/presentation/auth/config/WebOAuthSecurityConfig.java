package com.spring_greens.presentation.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.impl.AAD;
import com.spring_greens.presentation.auth.security.filter.CustomLogoutFilter;
import com.spring_greens.presentation.auth.security.filter.JsonAuthenticationFilter;
import com.spring_greens.presentation.auth.security.handler.CustomFailureHandler;
import com.spring_greens.presentation.auth.security.handler.CustomSuccessHandler;
import com.spring_greens.presentation.auth.security.handler.JwtAccessDeniedHandler;
import com.spring_greens.presentation.auth.security.handler.JwtAuthenticationEntryPoint;
import com.spring_greens.presentation.auth.security.filter.JwtAuthenticationFilter;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.auth.service.OAuth2Service;
import com.spring_greens.presentation.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final String[] PUBLIC_URLS_GLOBAL = {"/", "/error","/main", "/exception/**"};
    private final String[] PUBLIC_URLS_AUTH = {"/oauth2/authorization/**", "/login", "/*/login", "/login/**", "/*/login/**", "/logout", "/signup", "/*/signup", "/signup/**", "/*/signup/**"};
//    private final String[] AUTHORIZED_URLS_SOCIAL = {};
//    private final String[] AUTHORIZED_URLS_RETAILER = {};
//    private final String[] AUTHORIZED_URLS_WHOLESALER = {};

    private final OAuth2Service oAuth2Service;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private final UserService userService;


    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),   // 이미지 파일 요청
                        new AntPathRequestMatcher("/css/**"),   // CSS 파일 요청
                        new AntPathRequestMatcher("/js/**"),    // JavaScript 파일 요청
                        new AntPathRequestMatcher("/favicon.ico"), // 사이트의 파비콘 요청
                        new AntPathRequestMatcher("/resources/**"), // 리소스 요청
                        new AntPathRequestMatcher("/swagger-ui/**"), // Swagger UI 요청
                        new AntPathRequestMatcher("/v2/api-docs") // Swagger API 문서 요청
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // csrf disable
//                .formLogin(AbstractHttpConfigurer::disable) // form 로그인 방식(default 방식) disable
                .httpBasic(AbstractHttpConfigurer::disable) // http basic 인증 방식 disable
                .logout(AbstractHttpConfigurer::disable)// logout disable
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(JsonAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtProvider, objectMapper), LogoutFilter.class)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(PUBLIC_URLS_GLOBAL).permitAll() // Global
                                .requestMatchers(PUBLIC_URLS_AUTH).permitAll() // Auth
//                        .requestMatchers(AUTHORIZED_URLS_SOCIAL).hasRole("ROLE_SOCIAL") // AUTHORIZED Social
//                        .requestMatchers(AUTHORIZED_URLS_RETAILER).hasRole("ROLE_RETAILER") // AUTHORIZED Restailer
//                        .requestMatchers(AUTHORIZED_URLS_WHOLESALER).hasRole("ROLE_WHOLESALER") // AUTHORIZED Wholsesaler
                                .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2Service))
                        .successHandler(customSuccessHandler())
                        .failureHandler(customFailureHandler())
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
                                .accessDeniedHandler(jwtAccessDeniedHandler())
                )
                .build();
    }

    @Bean
    public CustomSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler(jwtProvider, jwtProperties, objectMapper);
    }

    @Bean
    public CustomFailureHandler customFailureHandler() {
        return new CustomFailureHandler(objectMapper);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(jwtProvider, jwtProperties, objectMapper);
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler(objectMapper);
    }

    public PasswordEncoder passwordEncoder(){
        DelegatingPasswordEncoder delegatingPasswordEncoder =
    			(DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

        return delegatingPasswordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userService);
        return new ProviderManager(provider);
    }
    
    @Bean
    public JsonAuthenticationFilter JsonAuthenticationFilter() {
        JsonAuthenticationFilter jsonAuthenticationFilter
            = new JsonAuthenticationFilter(objectMapper);
        jsonAuthenticationFilter.setAuthenticationManager(authenticationManager());
        jsonAuthenticationFilter.setAuthenticationSuccessHandler(customSuccessHandler());
        jsonAuthenticationFilter.setAuthenticationFailureHandler(customFailureHandler());
        return jsonAuthenticationFilter;
    }
}