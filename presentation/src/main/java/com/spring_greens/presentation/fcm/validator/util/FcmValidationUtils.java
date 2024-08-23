package com.spring_greens.presentation.fcm.validator.util;

import com.spring_greens.presentation.global.enums.Role;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FcmValidationUtils {
    private static final long IMAGE_MAX_SIZE = 1024 * 1024;
    private static final List<String> extensionList = new ArrayList<>(List.of(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "webp",
            "tiff"
    ));
    /**
     * Role validation
     * @param role
     * @param expectedRole
     */
    public static void validateRole(String role, String expectedRole) {
        if (role == null || !role.equals(expectedRole)) {
            throw new IllegalArgumentException("Don't have permission");
        }
    }

    /**
     * Role validation for 'saveFcmToken'
     * @param role
     */
    public static void validateRoleForSaveFcmToken(String role) {
        if(role.equals(Role.ROLE_SOCIAL.getRoleName())) {
            throw new IllegalArgumentException("Don't have permission");
        }
    }

    /**
     * ShopId validation
     * @param shopId
     */
    public static void validateShopId(Long shopId) {
        if (shopId == null) {
            throw new NullPointerException("Shop ID is null");
        }
    }

    /**
     * FcmToken validation
     * @param token
     */
    public static void validateFcmToken(String fcmToken) {
        if(fcmToken == null) {
            throw new NullPointerException("FcmToken is null");
        }

        if(fcmToken.getBytes().length > 4096) {
            throw new IllegalArgumentException("FcmToken bytes length exceeds allowed limits");
        }
    }

    /**
     * ImageUrl validation
     * @param imageUrl
     */
    public static void validateImage(MultipartFile image){

        if(image == null) {
            throw new NullPointerException("Image is null");
        }

        if(image.getSize() > IMAGE_MAX_SIZE) {
            throw new IllegalArgumentException("ImageFile size exceeds allowed limits");
        }

        String originalFilename = image.getOriginalFilename();
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("OriginalFilename is invalid");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if(!extensionList.contains(extension)) {
            throw new IllegalArgumentException("OriginalFilename Extension is invalid");
        }
    }

    /**
     * Title validation
     * @param title
     */
    public static void validateTitle(String title) {
        if(title == null) {
            throw new NullPointerException("Title is null");
        }

        if(title.isEmpty()) {
            throw new IllegalArgumentException("Title is empty");
        }

        // bytes limit is 60byte based on utf-8
        // 한글 20자, 영문 60자.
        // 한글 3bytes/word
        // 영문 1bytes/word
        if(title.getBytes(StandardCharsets.UTF_8).length > 60) {
            throw new IllegalArgumentException("Title bytes exceeds allowed limits");
        }

    }

    /**
     * Body validation
     * @param body
     */
    public static void validateBody(String body) {

        if(body == null) {
            throw new NullPointerException("Body is null");
        }

        if(body.isEmpty()) {
            throw new IllegalArgumentException("Body is empty");
        }
        // bytes limit is 450 bytes
        // fcm message total payload size limit 4KB(4000bytes)
        // title + body + imageSize = 60b + 450b + imageUrl(40b~60b < 1kb) < 4Kb
        if(body.getBytes(StandardCharsets.UTF_8).length > 450) {
            throw new IllegalArgumentException("Body bytes exceeds allowed limits");
        }
    }

}
