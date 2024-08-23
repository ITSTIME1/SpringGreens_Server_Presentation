package com.spring_greens.presentation.fcm.converter;

import com.spring_greens.presentation.fcm.converter.ifs.FcmConverter;
import com.spring_greens.presentation.fcm.dto.request.FcmServiceRequest;
import com.spring_greens.presentation.fcm.entity.FcmServiceRequestDetails;
import com.spring_greens.presentation.fcm.entity.FcmSubscription;
import com.spring_greens.presentation.fcm.entity.FcmToken;
import com.spring_greens.presentation.fcm.entity.FcmTopic;
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
                .userId(memberId)
                .token(fcmToken)
                .createdDateTime(createdDateTime)
                .registrationDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public FcmSubscription createFcmSubscription(Long memberId, String topicName) {
        return FcmSubscription.builder().userId(memberId).topicName(topicName).build();
    }

//    @Override
//    public FcmReservationMessageApm createFcmReservationMessage(FcmReserveRequest fcmReserveRequest, String imagePath, String topicName) {
//        return FcmReservationMessageApm.builder()
//                .title(fcmReserveRequest.getTitle())
//                .body(fcmReserveRequest.getBody())
//                .reserveDateTime(fcmReserveRequest.getReserveDateTime())
//                .imagePath(imagePath)
//                .topicName(topicName)
//                .build();
//    }

    @Override
    public FcmServiceRequest createFcmServiceRequest(Long userId, String role) {
        return FcmServiceRequest.builder().role(role).userId(userId).build();
    }
}
