package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.http.codec.multipart.FilePart;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

class NotEmptyMultipartListConstraintValidator implements ConstraintValidator<NotEmptyMultipartList, List<FilePart>> {

    @Override
    public boolean isValid(List<FilePart> files, ConstraintValidatorContext context) {

        return files != null && !files.isEmpty();
    }
}
