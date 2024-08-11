package com.spring_greens.presentation.fcm.validator;

import com.spring_greens.presentation.fcm.validator.ifs.FcmServiceValidator;
import com.spring_greens.presentation.fcm.validator.ifs.FcmValidatable;
import org.springframework.stereotype.Component;

@Component
public final class FcmServiceValidatorImpl implements FcmServiceValidator {
    @Override
    public void validate(FcmValidatable object) {
        object.validate();
    }
}
