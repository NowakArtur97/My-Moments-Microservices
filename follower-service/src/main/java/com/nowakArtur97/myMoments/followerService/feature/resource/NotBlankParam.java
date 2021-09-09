package com.nowakArtur97.myMoments.followerService.feature.resource;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = NotBlankParamConstraintValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface NotBlankParam {

    String message() default "Param is empty.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
