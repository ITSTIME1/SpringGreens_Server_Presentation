package com.spring_greens.presentation.fcm.converter.ifs;

import com.spring_greens.presentation.fcm.dto.request.FcmReserveRequest;
import com.spring_greens.presentation.fcm.entity.*;

import java.time.LocalDateTime;

public interface FcmConverter {

    FcmServiceRequestDetails createFcmServiceRequestDetails(Long shopId);

    FcmTopic createFcmTopic(Long shopId, String topicName);

    FcmToken createFcmToken(Long memberId, String fcmToken, LocalDateTime createdDateTime);

    FcmSubscription createFcmSubscription(Long memberId, String topicName);

    FcmReservationMessage createFcmReservationMessage(FcmReserveRequest fcmReserveRequest, String imagePath, String topicName);
}
