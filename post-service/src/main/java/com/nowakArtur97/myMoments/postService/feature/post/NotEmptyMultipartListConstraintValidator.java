package com.nowakArtur97.myMoments.postService.feature.post;

import org.bson.types.Binary;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

class NotEmptyMultipartListConstraintValidator implements ConstraintValidator<NotEmptyMultipartList, List<Binary>> {

    @Override
    public boolean isValid(List<Binary> files, ConstraintValidatorContext context) {

        return files != null && !files.isEmpty();
    }
}
