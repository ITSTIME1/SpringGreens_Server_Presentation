package com.spring_greens.presentation.fcm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FcmReservationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "reserve_date_time")
    private LocalDateTime reserveDateTime;

    private String title;

    @Column(name = "topic_name")
    private String topicName;

    private Boolean published;
}
