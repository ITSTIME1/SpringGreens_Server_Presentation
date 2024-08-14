package com.spring_greens.presentation.auth.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jdk.jfr.Description;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Table(name = "shop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    
    @Builder
    public Shop(Long id, String contact, String name, String intro, Boolean profileType, String roadAddress, String addressDetails, LocalTime startTime, LocalTime endTime, LocalDateTime registrationDateTime){
        this.id = id;
        this.contact = contact;
        this.name = name;
        this.intro = intro;
        this.profileType = profileType;
        this.roadAddress = roadAddress;
        this.addressDetails = addressDetails;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registrationDateTime = registrationDateTime;
    }
}
