package com.spring_greens.presentation.fcm.controller;

import com.spring_greens.presentation.auth.dto.CustomUser;
import com.spring_greens.presentation.fcm.dto.request.FcmReserveRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSaveTokenRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmServiceRequest;
import com.spring_greens.presentation.fcm.dto.request.FcmSubscriptionRequest;
import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.controller.AbstractBaseController;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.factory.service.ifs.ServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/api/fcm")
public class FcmController extends AbstractBaseController {


    public FcmController(ConverterFactory converterFactory, ServiceFactory serviceFactory) {
        super(converterFactory, serviceFactory);
    }

    @PostMapping("/register/fcm_service_details")
    @Operation(summary = "Fcm 서비스 신청(도매업자)", description = "도매업자용 Fcm서비스 신청 API")
    public ApiResponse<?> registerFcmServiceRequest(@AuthenticationPrincipal CustomUser customUser) {

        // 1. 컨버터를 통해서, 요청서를 만듦.
        FcmServiceRequest fcmServiceRequest = converterFactory.getFcmConverter().createFcmServiceRequest(customUser.getId(), customUser.getRole());
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
    public ApiResponse<?> registerFcmToken(@AuthenticationPrincipal CustomUser customUser,
                                           @RequestBody FcmSaveTokenRequest fcmSaveTokenRequest) {

        log.info("RegisterFcmToken");
        FcmSaveTokenRequest modifyFcmSaveRequest = fcmSaveTokenRequest.toBuilder()
                .role(customUser.getRole())
                .memberId(customUser.getId())
                .build();

        boolean result = serviceFactory.getFcmService().registerFcmToken(modifyFcmSaveRequest);
        if(!result){
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    // 이걸 만들어보자.

    @PostMapping("/reserve/fcm")
    @Operation(summary = "Fcm 예약 메세지 발송", description = "도매업자가 가게관리에서 발행한 예약메세지 저장 API")
    public ApiResponse<?> reserveMessage(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam("reserveDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime reserveDateTime,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal CustomUser customUser) {

        // Create a request object with the provided data
        FcmReserveRequest reserveRequest = FcmReserveRequest.builder()
                .memberId(customUser.getId())
                .role(customUser.getRole())
                .title(title)
                .body(body)
                .reserveDateTime(reserveDateTime)
                .build();

        // Handle the image file if present
        if (image != null && !image.isEmpty()) {
            // Process the image (e.g., save it or attach it to the request)
            reserveRequest = reserveRequest.toBuilder().image(image).build();
        }

        boolean result = serviceFactory.getFcmService().reserveFcmMessage(reserveRequest);
        if (!result) {
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }



}
