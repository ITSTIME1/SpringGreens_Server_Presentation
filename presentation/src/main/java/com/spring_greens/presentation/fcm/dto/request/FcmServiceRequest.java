package com.spring_greens.presentation.fcm.dto.request;

import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import com.spring_greens.presentation.global.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmServiceRequest implements FcmValidatable {
    private Long userId;
    private String role;
    private String fcmToken;

    @Override
    public void validate() {
        FcmValidationUtils.validateRole(this.role, Role.ROLE_WHOLESALER.getRoleName());
    }

}
