//package com.spring_greens.presentation.auth.security.Jwt;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//import javax.crypto.SecretKey;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import com.spring_greens.presentation.auth.dto.TokenDTO;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class JwtTokenProvider {
//    private final SecretKey key;
//
//    public JwtTokenProvider(@Value("${jwt.secret}") String secretkey){
//        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public TokenDTO generateToken(Authentication authentication){
//        String authorities = authentication.getAuthorities().stream()
//        .map(GrantedAuthority::getAuthority)
//        .collect(Collectors.joining(","));
//
//
//        long now = (new Date()).getTime();
//
//        Date accessTokenExpiresln = new Date(now + 1800000);
//        String accessToken = Jwts.builder()
//            .subject(authentication.getName())
//            .claim("auth", authorities)
//            .expiration(accessTokenExpiresln)
//            .signWith(key, Jwts.SIG.HS512)
//            .compact();
//
//        String refreshToken = Jwts.builder()
//            .expiration(new Date(now + 86400000))
//            .signWith(key, Jwts.SIG.HS512)
//            .compact();
//
//        return TokenDTO.builder()
//        .grantType("Bearer")
//        .accessToken(accessToken)
//        .refreshToken(refreshToken)
//        .build();
//
//    }
//
//    public Authentication getAuthentication(String accessToken){
//        Claims claims = parseClaims(accessToken);
//
//        if(claims.get("auth")==null){
//            throw new RuntimeException("권한정보가 없는 토큰입니다.");
//        }
//
//        Collection<? extends GrantedAuthority> authorities =
//        Arrays.stream(claims.get("auth")
//            .toString().split(","))
//            .map(SimpleGrantedAuthority::new)
//            .collect(Collectors.toList());
//
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
//    }
//
//    public boolean vaildateToken(String token){
//        try{
//            Jwts.parser()
//            .verifyWith(key)
//            .build()
//            .parseSignedClaims(token);
//
//            return true;
//        }catch (JwtException e){
//            log.info("Unsupported JWT Exception", e);
//        }catch (IllegalArgumentException e){
//            log.info("JWT Claims string is empty", e);
//        }
//        return false;
//    }
//
//    private Claims parseClaims(String accessToken){
//        try{
//            return Jwts.parser()
//            .verifyWith(key)
//            .build()
//            .parseSignedClaims(accessToken)
//            .getPayload();
//        }catch (JwtException e){
//            log.info("Unsupported JWT Exception", e);
//            Claims claims = Jwts.claims().build();
//            claims.put("auth", null);
//            return claims;
//        }catch (IllegalArgumentException e){
//            log.info("JWT Claims string is empty", e);
//            Claims claims = Jwts.claims().build();
//            claims.put("auth", null);
//            return claims;
//        }
//    }
//}
