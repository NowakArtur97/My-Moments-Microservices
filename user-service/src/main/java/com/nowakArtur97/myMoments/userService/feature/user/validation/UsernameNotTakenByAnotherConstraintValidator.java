package com.nowakArtur97.myMoments.userService.feature.user.validation;

import com.nowakArtur97.myMoments.userService.feature.user.document.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UsernameNotTakenByAnotherConstraintValidator implements ConstraintValidator<UsernameNotTakenByAnother, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameInContext = auth != null ? auth.getName() : "";

        if (usernameInContext.equals("")) {
            return false;
        }

        return usernameInContext.equals(username) || !userService.isUsernameAlreadyInUse(username);
    }
}