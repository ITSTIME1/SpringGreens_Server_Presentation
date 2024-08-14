package com.spring_greens.presentation.auth.dto.oauth;

/**
 * Defines the response structure for OAuth2 authentication providers.
 * <p>
 * Implementations of this interface should provide methods to access information
 * returned from third-party OAuth2 providers (e.g., Google, Naver, Kakao). It includes methods
 * to retrieve details such as the provider name, provider-specific user ID, email address,
 * and the user's real name.
 * </p>
 * <p>
 * Implementations of this interface are expected to handle the specifics of various OAuth2
 * providers and extract the necessary user information from their responses. This structure
 * ensures consistency in handling authentication responses across different providers.
 * </p>
 *
 * @author 01223lsh
 */
public interface OAuth2Response {

    // Third-party provider name (e.g., Naver, Google, ...)
    String getProvider();

    // Provider-specific user ID
    String getProviderId();

    // Email obtained from the OAuth2 provider
    String getEmail();

    // Name obtained from OAuth2 provider
    String getName();
}