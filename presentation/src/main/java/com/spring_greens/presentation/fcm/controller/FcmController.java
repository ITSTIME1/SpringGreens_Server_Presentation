package com.spring_greens.presentation.fcm.controller;

import com.spring_greens.presentation.fcm.dto.request.FcmReserveRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSaveTokenRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmServiceRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSubscriptionRequest;
import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.controller.AbstractBaseController;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.factory.service.ifs.ServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/fcm")
public class FcmController extends AbstractBaseController {


    public FcmController(ConverterFactory converterFactory, ServiceFactory serviceFactory) {
        super(converterFactory, serviceFactory);
    }

    @PostMapping("/register/fcm_service_details")
    @Operation(summary = "Fcm 서비스 신청(도매업자)", description = "도매업자용 Fcm서비스 신청 API")
    public ApiResponse<?> registerFcmServiceRequest(@RequestBody @Valid FcmServiceRequest fcmServiceRequest) {
        boolean result = serviceFactory.getFcmService().registerFcmService(fcmServiceRequest);
        if(!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }


    @PostMapping("/subscribe/fcm_service")
    @Operation(summary = "Fcm 서비스 구독 신청(소매업자)", description = "소매업자용 Fcm서비스 구독 API")
    public ApiResponse<?> subscribeFcmService(@RequestBody FcmSubscriptionRequest fcmSubscriptionRequest) {
        FcmSubscriptionRequest subscribeRequest = fcmSubscriptionRequest.toBuilder()
                .memberId(1L)
                .role(fcmSubscriptionRequest.getRole())
                .build();

        boolean result = serviceFactory.getFcmService().subscribeFcmTopic(subscribeRequest);
        if(!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    @PostMapping("/register/fcm_token")
    @Operation(summary = "Fcm 토큰 저장", description = "클라이언트에서 발급받은 Fcm토큰을 서버로 저장 API")
    public ApiResponse<?> registerFcmToken(@RequestBody FcmSaveTokenRequest fcmSaveTokenRequest) {
        boolean result = serviceFactory.getFcmService().registerFcmToken(fcmSaveTokenRequest);
        if(!result){
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    @PostMapping("/reserve/fcm")
    @Operation(summary = "Fcm 예약 메세지 발송", description = "도매업자가 가게관리에서 발행한 예약메세지 저장 API")
    public ApiResponse<?> reserveMessage(@ModelAttribute FcmReserveRequest fcmReserveRequest) {
        boolean result = serviceFactory.getFcmService().reserveFcmMessage(fcmReserveRequest);
        if(!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }



}
