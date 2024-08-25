package com.spring_greens.presentation.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FcmSaveTokenRequest implements FcmValidatable {
    private Long memberId;
    private String role;
    private String fcmToken;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    @Override
    public void validate() {
        FcmValidationUtils.validateRoleForSaveFcmToken(this.role);
        FcmValidationUtils.validateFcmToken(this.fcmToken);
    }
}
