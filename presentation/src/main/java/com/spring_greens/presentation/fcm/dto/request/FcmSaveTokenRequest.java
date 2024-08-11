package com.spring_greens.presentation.fcm.dto.request;

import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class FcmSaveTokenRequest implements FcmValidatable {
    private Long memberId;
    private String role;
    private String fcmToken;
    private LocalDateTime createdDateTime;

    @Override
    public void validate() {
        FcmValidationUtils.validateRoleForSaveFcmToken(this.role);
        FcmValidationUtils.validateFcmToken(this.fcmToken);
    }
}
