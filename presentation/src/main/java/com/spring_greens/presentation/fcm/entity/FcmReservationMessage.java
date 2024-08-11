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
public class FcmReservationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "topic_name")
    private String topicName;

    private String title;

    private String body;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "reserve_date_time")
    private LocalDateTime reserveDateTime;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean published;

}
