package com.nowakArtur97.myMoments.userService.feature.user.validation;

import com.nowakArtur97.myMoments.userService.feature.user.document.UserDocument;
import com.nowakArtur97.myMoments.userService.feature.user.document.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
class EmailNotTakenByAnotherConstraintValidator implements ConstraintValidator<EmailNotTakenByAnother, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameInContext = auth != null ? auth.getName() : "";

        Optional<UserDocument> userOptional = userService.findByUsername(usernameInContext);

        if (userOptional.isEmpty()) {
            return false;
        }

        return userOptional.get().getEmail().equals(email) || !userService.isEmailAlreadyInUse(email);
    }
}