package com.spring_greens.presentation.fcm.dto.request;

import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FcmReserveRequest implements FcmValidatable {
    private Long memberId;
    private Long shopId;
    private String title;
    private String body;
    private LocalDateTime reserveDateTime;
    private MultipartFile image;

    @Override
    public void validate() {
        FcmValidationUtils.validateShopId(this.shopId);
        FcmValidationUtils.validateTitle(this.title);
        FcmValidationUtils.validateBody(this.body);
        FcmValidationUtils.validateImage(this.image);
    }

}
