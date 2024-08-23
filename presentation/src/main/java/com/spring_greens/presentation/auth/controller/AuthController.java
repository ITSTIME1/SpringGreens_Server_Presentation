
package com.spring_greens.presentation.auth.controller;

import com.spring_greens.presentation.auth.dto.request.RetailSignupRequest;
import com.spring_greens.presentation.auth.dto.request.WholesaleSignupRequest;
import com.spring_greens.presentation.global.api.ApiResponse;
import com.spring_greens.presentation.global.controller.AbstractBaseController;
import com.spring_greens.presentation.global.factory.converter.ifs.ConverterFactory;
import com.spring_greens.presentation.global.factory.service.ifs.ServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController extends AbstractBaseController {

    public AuthController(ConverterFactory converterFactory, ServiceFactory serviceFactory) {
        super(converterFactory, serviceFactory);
    }

    @PostMapping("/signup/retail")
    @Operation(summary = "소매 회원가입", description = "소매업자 회원가입 요청 API")
    public ApiResponse<?> registerRetailRequest(@RequestBody RetailSignupRequest RetailSignupRequest) {
        boolean result = serviceFactory.getUserService().retailRegister(RetailSignupRequest);
        if(!result){
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }

    @PostMapping("/signup/wholesale")
    @Operation(summary = "도매 회원가입", description = "도매업자 회원가입 요청 API")
    public ApiResponse<?> postMethodName(@RequestBody WholesaleSignupRequest wholesaleSignupRequest) {
        boolean result = serviceFactory.getUserService().wholesaleRegister(wholesaleSignupRequest);
        if(!result){
            return ApiResponse.fail();
        }
        return ApiResponse.ok();
    }
}
