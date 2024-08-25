package com.spring_greens.presentation.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import com.spring_greens.presentation.fcm.validator.util.FcmValidationUtils;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class FcmReserveRequest implements FcmValidatable {
    private Long memberId;
    private Long shopId;
    private String role;
    private String title;
    private String body;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Make sure the pattern matches the expected format
    private LocalDateTime reserveDateTime;
    private MultipartFile image;

    @Override
    public void validate() {
        FcmValidationUtils.validateTitle(this.title);
        FcmValidationUtils.validateBody(this.body);
    }

}
