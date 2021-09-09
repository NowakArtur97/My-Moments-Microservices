package com.nowakArtur97.myMoments.followerService.feature.resource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class NotBlankParamConstraintValidator implements ConstraintValidator<NotBlankParam, String> {

    @Override
    public boolean isValid(String param, ConstraintValidatorContext context) {

        return param != null && !param.isBlank();
    }
}
