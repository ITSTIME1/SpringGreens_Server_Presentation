package com.spring_greens.presentation.shop.entity;

import jakarta.persistence.*;
import jdk.jfr.Description;

import java.time.LocalDateTime;
import java.time.LocalTime;

// @TODO 리팩토링
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "member_id")
    private Long memberId;
    private String contact;
    private String name;
    private String intro;
    @Column(name = "profile_type")
    @Description("프로필 설정 여부")
    private Boolean profileType;
    @Column(name = "road_address")
    @Description("도로명주소")
    private String roadAddress;

    @Column(name = "address_details")
    @Description("상세주소")
    private String addressDetails;

    @Column(name = "start_time")
    @Description("영업시작시간")
    private LocalTime startTime;

    @Column(name = "end_time")
    @Description("영업종료시간")
    private LocalTime endTime;

    @Column(name = "registration_date")
    @Description("가게등록일")
    private LocalDateTime registrationDateTime;

}
