package com.spring_greens.presentation.auth.dto;

import com.spring_greens.presentation.global.enums.Role;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class WholesaleSignupDTO {
    private Role role;
    private String email;
    private String password;
    private String contact;
    private String businessNumber;
    private String name;
    private boolean alertType;
    private boolean termsType;
    private boolean socialType;
    private String socialName;
    private String roadAddress;
    private String addressDetails;
    private String shopName;
    private String shopContact;
    private String intro;
    private String shopRoadAddress;
    private String shopAddressDetail;
    private boolean profileType;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime registrationDateTime;
}
