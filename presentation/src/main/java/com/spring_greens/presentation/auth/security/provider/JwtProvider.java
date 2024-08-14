package com.spring_greens.presentation.auth.security.provider;

import com.spring_greens.presentation.auth.config.JwtProperties;
import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.UserDTO;
import com.spring_greens.presentation.auth.entity.RefreshToken;
import com.spring_greens.presentation.global.enums.Role;
import com.spring_greens.presentation.auth.exception.JwtException;
import com.spring_greens.presentation.auth.repository.RefreshTokenRepository;
import com.spring_greens.presentation.global.enums.JwtErrorCode;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Provides utility methods for generating, validating, and parsing JWT tokens.
 * <p>
 * This class handles JWT token operations including generation of access and refresh tokens,
 * validation of tokens, and interaction with the database for refresh tokens.
 * </p>
 * <p>
 * including:
 *  * <ul>
 *  *     <li>Generating new access tokens with an expiration time based on configured properties.</li>
 *  *     <li>Generating new refresh tokens, saving them in the database, and associating them with user ID.</li>
 *  *     <li>Validating JWT tokens by checking for expiration, signature, and other potential issues.</li>
 *  *     <li>Parsing JWT tokens to retrieve user details and authentication information.</li>
 *  *     <li>Retrieving refresh tokens from the database using user IDs.</li>
 *  *     <li>Deleting refresh tokens from the database for specified user IDs.</li>
 *  * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;

    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String TOKEN_PREFIX = "Bearer ";
    public static final String Access_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    @Autowired
    public JwtProvider(JwtProperties jwtProperties, RefreshTokenRepository refreshTokenRepository) {
        this.jwtProperties = jwtProperties;
        this.secretKey = jwtProperties.getSecretKey();  // Initialize the SecretKey from JwtProperties to avoid repeated key generation.
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Generates a new access token for the given user.
     * <p>
     * The access token is created with an expiration time based on the configured properties.
     * </p>
     */
    public String generateAccessToken(CustomUser customUser) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + jwtProperties.getAccessTokenExpiration()), customUser, Access_TOKEN_NAME);
    }

    /**
     * Generates a new refresh token and stores it in the database.
     * <p>
     * The refresh token is created with an expiration time based on the configured properties and
     * then saved in the database associated with the user's ID.
     * </p>
     */
    @Transactional
    public String generateRefreshToken(CustomUser customUser) {
        Date now = new Date();
        String token = makeToken(new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration()), customUser, REFRESH_TOKEN_NAME);
        RefreshToken refreshToken = new RefreshToken(customUser.getId(), token);
        refreshTokenRepository.insertOrUpdateRefreshToken(token, customUser.getId());
        return token;
    }

    /**
     * Creates a JWT token for the given user with the specified expiration date.
     * <p>
     * Builds a JWT token using the provided expiration date and user details.
     * The token includes claims such as user ID, name, and role, and is signed with the specified secret key.
     * </p>
     */
    private String makeToken(Date expiry, CustomUser customUser, String tokenType) {
        Date now = new Date();

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .subject(customUser.getEmail())
                .claim("token_type", tokenType)
                .claim("id", customUser.getId())
                .claim("name", customUser.getName())
                .claim("role", customUser.getRole())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Validates the given JWT token.
     * <p>
     * Checks the token for validity, including expiration and signature. Logs and throws
     * appropriate exceptions if the token is invalid.
     * </p>
     */
    public boolean validToken(String token, String tokenType) {
        // java.lang.IllegalArgumentException: CharSequence cannot be null or empty.
        if (token == null || token.trim().isEmpty()) {
            log.error(JwtErrorCode.UNKNOWN_TOKEN.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.UNKNOWN_TOKEN);
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token).getPayload();

            if(tokenType.equals(Access_TOKEN_NAME)) {
                if (!claims.get("token_type").equals(tokenType)) {
                    log.info(JwtErrorCode.INVALID_TYPE_TOKEN.getMessage());
                    throw new JwtException.JwtNotValidateException(JwtErrorCode.INVALID_TYPE_TOKEN);
                }
            }

            if (claims.getExpiration().before(new Date())) {
                log.info(JwtErrorCode.EXPIRED_TOKEN.getMessage());
                throw new JwtException.JwtNotValidateException(JwtErrorCode.EXPIRED_TOKEN);
            }
            
            return true;
        } catch (SignatureException e) {
            log.error("{} | exception : {}", JwtErrorCode.WRONG_SIGNATURE_TOKEN.getMessage(), e.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.WRONG_SIGNATURE_TOKEN, e);
        } catch (MalformedJwtException e) {
            log.error("{} | exception : {}", JwtErrorCode.MALFORMED_TOKEN.getMessage(), e.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.MALFORMED_TOKEN, e);
        } catch (ExpiredJwtException e) {
            log.error("{} | exception : {}", JwtErrorCode.EXPIRED_TOKEN.getMessage(), e.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.EXPIRED_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            log.error("{} | exception : {}", JwtErrorCode.UNSUPPORTED_TOKEN.getMessage(), e.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.UNSUPPORTED_TOKEN, e);
        } catch (IllegalArgumentException | DecodingException | WeakKeyException e) {
            log.error("{} | exception : {}", JwtErrorCode.INVALID_CLAIMS_TOKEN.getMessage(), e.getMessage());
            throw new JwtException.JwtNotValidateException(JwtErrorCode.INVALID_CLAIMS_TOKEN, e);
        }
    }

    /**
     * Parses the given JWT token and retrieves its claims.
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // ?���? ?�� ?��?��
                .build()
                .parseSignedClaims(token).getPayload();
    }

    /**
     * Gets the authentication information from the given JWT token.
     * <p>
     * Parses the token to extract user details and authorities for authentication.
     * </p>
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        UserDTO userDTO = UserDTO.builder()
                .email(claims.getSubject())
                .id(claims.get("id", Long.class))
                .name(claims.get("name", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();

        CustomUser customUser = new CustomUser(userDTO);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(claims.get("role", String.class)));

        return new UsernamePasswordAuthenticationToken(customUser, token, authorities);
    }

    /**
     * Gets the CustomUser object from the given JWT token.
     * <p>
     * Parses the token to extract user details and returns a CustomUser object.
     * </p>
     */
     public CustomUser getCustomUser(String token) {
        Claims claims = getClaims(token);

        UserDTO userDTO = UserDTO.builder()
                .email(claims.getSubject())
                .id(claims.get("id", Long.class))
                .name(claims.get("name", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();

        return new CustomUser(userDTO);
    }

    /**
     * Gets the refresh token associated with a user from the database.
     * <p>
     * Finds and returns the refresh token for the specified user ID.
     * </p>
     */
    public RefreshToken getRefreshTokenFromDB(long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElse(null);
    }

    /**
     * Deletes the refresh token for the specified user from the database.
     * <p>
     * Removes the refresh token associated with the specified user ID.
     * </p>
     */
    @Transactional
    public void deleteRefreshTokenFromDB(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}