package com.nowakArtur97.myMoments.userService.domain.validation;

import com.nowakArtur97.myMoments.userService.domain.document.UserService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueUsernameConstraintValidator implements ConstraintValidator<UniqueUsername, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        return !userService.isUsernameAlreadyInUse(username);
    }
}