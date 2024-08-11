package com.spring_greens.presentation.fcm.converter;

import com.spring_greens.presentation.fcm.converter.ifs.FcmConverter;
import com.spring_greens.presentation.fcm.dto.request.FcmReserveRequest;
import com.spring_greens.presentation.fcm.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FcmConverterImpl implements FcmConverter {

    @Override
    public FcmServiceRequestDetails createFcmServiceRequestDetails(Long shopId) {
        return FcmServiceRequestDetails.builder()
                // 현재 shopId가 임시임
                .shopId(1L)
                .registrationDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public FcmTopic createFcmTopic(Long shopId, String topicName) {
        return FcmTopic.builder()
                .shopId(shopId)
                .fcmTopicName(topicName)
                .registrationDateTime(LocalDateTime.now())
                .build();

    }

    @Override
    public FcmToken createFcmToken(Long memberId, String fcmToken, LocalDateTime createdDateTime) {
        return FcmToken.builder()
                .memberId(memberId)
                .token(fcmToken)
                .createdDateTime(createdDateTime)
                .registrationDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public FcmSubscription createFcmSubscription(Long memberId, String topicName) {
        return FcmSubscription.builder().memberId(memberId).topicName(topicName).build();
    }

    @Override
    public FcmReservationMessage createFcmReservationMessage(FcmReserveRequest fcmReserveRequest, String imagePath, String topicName) {
        return FcmReservationMessage.builder()
                .title(fcmReserveRequest.getTitle())
                .body(fcmReserveRequest.getBody())
                .reserveDateTime(fcmReserveRequest.getReserveDateTime())
                .imagePath(imagePath)
                .topicName(topicName)
                .build();
    }
}
