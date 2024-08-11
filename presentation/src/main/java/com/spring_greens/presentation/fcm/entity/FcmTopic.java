package com.spring_greens.presentation.fcm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "fcm_topic_name")
    private String fcmTopicName;

    @Column(name = "total_subscriber", columnDefinition = "int default 0")
    private int totalSubscriber;

    @Column(name = "registration_date_time")
    private LocalDateTime registrationDateTime;
}
