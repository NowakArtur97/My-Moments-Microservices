package com.nowakArtur97.myMoments.userService.feature.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UsernameNotTakenByAnotherConstraintValidator.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface UsernameNotTakenByAnother {

    String message() default "Username is already taken by another user.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
