package com.spring_greens.presentation.fcm.dto.request;

import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmServiceRequest implements FcmValidatable {
    private Long memberId;
    private String role;
    private String fcmToken;

    @Override
    public void validate() {
        FcmValidationUtils.validateRole(this.role, "도매업자");
        FcmValidationUtils.validateFcmToken(this.fcmToken);
    }

}
