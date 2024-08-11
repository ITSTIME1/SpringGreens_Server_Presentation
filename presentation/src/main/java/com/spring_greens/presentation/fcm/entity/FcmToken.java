package com.spring_greens.presentation.fcm.entity;


import jakarta.persistence.*;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Description("기기 고유값 fcm token")
    private String token;

    @Column(name = "created_date_time")
    @Description("클라이언트에서 발행한 시간")
    private LocalDateTime createdDateTime;

    @Column(name = "registration_date_time")
    @Description("서버에 저장하는 시간")
    private LocalDateTime registrationDateTime;
}
