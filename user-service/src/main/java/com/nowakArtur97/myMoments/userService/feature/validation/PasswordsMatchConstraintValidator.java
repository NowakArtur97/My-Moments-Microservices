package com.nowakArtur97.myMoments.userService.feature.validation;

import com.nowakArtur97.myMoments.userService.feature.resource.UserDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class PasswordsMatchConstraintValidator implements ConstraintValidator<PasswordsMatch, UserDTO> {

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {

        return user.getPassword().equals(user.getMatchingPassword());
    }
}
