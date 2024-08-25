package com.spring_greens.presentation.auth.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class WholesaleSignupRequest {
    private String email;
    private String password;
    private String contact;
    private String businessNumber;
    private String name;
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
}
