package com.spring_greens.presentation.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetailSignupRequest {
    private String email;
    private String password;
    private String contact;
    private String businessNumber;
    private String name;
    private String roadAddress;
    private String addressDetails;
}
