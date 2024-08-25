package com.spring_greens.presentation.fcm.converter.ifs;

import com.spring_greens.presentation.fcm.dto.request.FcmServiceRequest;
import com.spring_greens.presentation.fcm.entity.FcmServiceRequestDetails;
import com.spring_greens.presentation.fcm.entity.FcmSubscription;
import com.spring_greens.presentation.fcm.entity.FcmToken;
import com.spring_greens.presentation.fcm.entity.FcmTopic;

import java.time.LocalDateTime;

public interface FcmConverter {

    FcmServiceRequestDetails createFcmServiceRequestDetails(Long shopId);

    FcmTopic createFcmTopic(Long shopId, String topicName);

    FcmToken createFcmToken(Long memberId, String fcmToken, LocalDateTime createdDateTime);

    FcmSubscription createFcmSubscription(Long memberId, String topicName);

//    FcmReservationMessageApm createFcmReservationMessage(FcmReserveRequest fcmReserveRequest, String imagePath, String topicName);

    FcmServiceRequest createFcmServiceRequest(Long userId, String role);
}
