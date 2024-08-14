package com.spring_greens.presentation.auth.service;

import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.auth.dto.oauth.OAuth2Response;
import com.spring_greens.presentation.auth.dto.UserDTO;
import com.spring_greens.presentation.auth.entity.User;
import com.spring_greens.presentation.auth.repository.UserRepository;
import com.spring_greens.presentation.global.enums.OAuth2ResponseEnum;
import com.spring_greens.presentation.global.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Service class to handle OAuth2 user authentication and management.
 * <p>
 * This service extends {@link DefaultOAuth2UserService} to load user details from OAuth2 providers.
 * It manages user registration and updates based on OAuth2 responses.
 * </p>
 * <p>
 * including:
 * <ul>
 *     <li>Loading user details from the OAuth2 provider and processing the user information.</li>
 *     <li>Registering new users if they are not found in the system.</li>
 *     <li>Updating existing user information if it matches the social provider.</li>
 *     <li>Creating and returning {@link CustomUser} objects based on user data.</li>
 *     <li>Finding users by their email addresses.</li>
 * </ul>
 * </p>
 *
 * @author 01223lsh
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    /**
     * Loads user details from the OAuth2 provider and processes the user.
     * <p>
     * Registers a new user if not found; updates existing user info.
     * </p>
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2Response oAuth2Response = createOAuth2Response(oAuth2User, userRequest.getClientRegistration().getRegistrationId());
        User existData = findByEmail(oAuth2Response.getEmail());

        if (existData == null) {
            return registerNewUser(oAuth2Response);
        } else {
            updateExistingUser(existData, oAuth2Response);
            return createCustomUserDTO(existData);
        }
    }

    /**
     * Creates an OAuth2Response from OAuth2 user attributes and registration ID.
     * <p>
     * Determines response type based on provider registration ID.
     * </p>
     */
    private OAuth2Response createOAuth2Response(OAuth2User oAuth2User, String registrationId) throws OAuth2AuthenticationException {
        OAuth2ResponseEnum oAuth2ResponseEnum = OAuth2ResponseEnum.getByRegistrationId(registrationId);
        if (oAuth2ResponseEnum == null) {
            throw new OAuth2AuthenticationException(String.format("Unsupported provider: %s", registrationId));
        }
        return oAuth2ResponseEnum.createResponse(oAuth2User.getAttributes());
    }

    /**
     * Registers a new user based on OAuth2 response.
     * <p>
     * Saves the user and returns a {@link CustomUser} with user details.
     * </p>
     */
    private CustomUser registerNewUser(OAuth2Response oAuth2Response) {
        User user = User.builder()
                .email(oAuth2Response.getEmail())
                .name(oAuth2Response.getName())
                .role(Role.ROLE_SOCIAL)
                .socialType(true)
                .socialName(oAuth2Response.getProvider())
                .build();

        userRepository.save(user);

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();

        log.info("New user registered: ID={}, Email={}, Name={}, Role={}", user.getId(), user.getEmail(), user.getName(), user.getRole());
        return new CustomUser(userDTO);
    }

    /**
     * Updates an existing user's information based on OAuth2 response.
     * <p>
     * Updates details if the social provider matches.
     * </p>
     */
    private void updateExistingUser(User existingUser, OAuth2Response oAuth2Response) {
        if (existingUser.getSocialName().equals(oAuth2Response.getProvider())) {
            existingUser.updateUserInfo(oAuth2Response.getEmail(), oAuth2Response.getName());
            userRepository.save(existingUser);
            log.info("User updated: ID={}, Email={}, Name={}", existingUser.getId(), existingUser.getEmail(), existingUser.getName());
        }
    }

    /**
     * Creates a {@link CustomUser} from a user entity.
     * <p>
     * Converts user entity to {@link UserDTO} and wraps it in {@link CustomUser}.
     * </p>
     */
    private CustomUser createCustomUserDTO(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .build();

        return new CustomUser(userDTO);
    }

    /**
     * Finds a user by email.
     * <p>
     * Searches for a user in the repository using their email address.
     * </p>
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}