package com.spring_greens.presentation.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_greens.presentation.auth.security.filter.CustomLogoutFilter;
import com.spring_greens.presentation.auth.security.filter.JsonAuthenticationFilter;
import com.spring_greens.presentation.auth.security.filter.JwtAuthenticationFilter;
import com.spring_greens.presentation.auth.security.handler.CustomFailureHandler;
import com.spring_greens.presentation.auth.security.handler.CustomSuccessHandler;
import com.spring_greens.presentation.auth.security.handler.JwtAccessDeniedHandler;
import com.spring_greens.presentation.auth.security.handler.JwtAuthenticationEntryPoint;
import com.spring_greens.presentation.auth.security.provider.JwtProvider;
import com.spring_greens.presentation.auth.service.OAuth2Service;
import com.spring_greens.presentation.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final String[] PUBLIC_URLS_GLOBAL = {"/", "/main", "/error", "/exception/**", "/api/product/**", "/api/map/**", "/ws/**"};
    private final String[] PUBLIC_URLS_AUTH = {"/oauth2/authorization/**", "/login", "/*/login", "/login/**", "/*/login/**", "/signup", "/signup/**"};

    /* Currently, these role-based restrictions are commented out due to missing URL definitions. */
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
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**"),
                        new AntPathRequestMatcher("/resources/**"),
                        new AntPathRequestMatcher("/swagger-ui/**"),
                        new AntPathRequestMatcher("/v2/api-docs")
                );
    }

    /**
     * Configures the security filter chain for the application.
     *
     * <p>
     * CSRF protection and HTTP Basic authentication are disabled.<br>
     * Session management is set to stateless, meaning no sessions are created or used.<br>
     * OAuth2 login is configured with a custom login page, user info endpoint service, and handlers for success and failure.<br>
     * Exception handling is configured with custom entry points and access denied handlers.<br>
     * Public URLs are divided by domain and accessible without authentication.<br>
     * Role-based access is required for certain URLs, but these restrictions are currently commented out due to missing URL definitions.<br>
     * All other requests require authentication.<br>
     * </p>
     *
     * <p>
     * Custom filters are added:
     * <ul>
     *     <li>JsonAuthenticationFilter is added after LogoutFilter. Handles standard login,</li>
     *     <li>JwtAuthenticationFilter is added before UsernamePasswordAuthenticationFilter. Validates JWT tokens.</li>
     *     <li>CustomLogoutFilter is added before LogoutFilter. Manages JWT-based logout.</li>
     * </ul>
     * </p>
     *
     * <p>
     * <b>Currently, these role-based restrictions are commented out due to missing URL definitions.</b>
     * </p>
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(JsonAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtProvider, objectMapper), LogoutFilter.class)
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
                .authorizeHttpRequests(auth -> auth

                                .requestMatchers(PUBLIC_URLS_GLOBAL).permitAll()
                                .requestMatchers(PUBLIC_URLS_AUTH).permitAll()
//                        .requestMatchers(AUTHORIZED_URLS_SOCIAL).hasRole("SOCIAL") // AUTHORIZED Social
//                        .requestMatchers(AUTHORIZED_URLS_RETAILER).hasRole("RETAILER") // AUTHORIZED Restailer
//                        .requestMatchers(AUTHORIZED_URLS_WHOLESALER).hasRole("WHOLESALER") // AUTHORIZED Wholsesaler
                                .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 허용할 출처
        configuration.addAllowedOrigin("https://spring-greens-client.vercel.app"); // 다른 허용 출처
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용 (쿠키 등)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용
        return source;
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

    @Bean
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