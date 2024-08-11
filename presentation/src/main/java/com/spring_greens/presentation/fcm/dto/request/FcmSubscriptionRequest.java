package com.spring_greens.presentation.fcm.dto.request;

import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class FcmSubscriptionRequest implements FcmValidatable {
    private String role;
    private Long memberId;
    private Long shopId;

    @Override
    public void validate() {
        FcmValidationUtils.validateRole(this.role, "소매업자");
        FcmValidationUtils.validateShopId(this.shopId);
    }
}
