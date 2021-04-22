package com.nowakArtur97.myMoments.userService.feature.user.validation;

import com.nowakArtur97.myMoments.userService.feature.user.document.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        return !userService.isEmailAlreadyInUse(email);
    }
}