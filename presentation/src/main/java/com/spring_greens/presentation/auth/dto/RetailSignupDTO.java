package com.spring_greens.presentation.auth.dto;

import com.spring_greens.presentation.global.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetailSignupDTO {
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
}
