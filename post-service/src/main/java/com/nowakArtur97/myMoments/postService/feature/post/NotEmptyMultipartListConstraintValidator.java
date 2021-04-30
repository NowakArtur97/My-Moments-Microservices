package com.nowakArtur97.myMoments.postService.feature.post;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

class NotEmptyMultipartListConstraintValidator implements ConstraintValidator<NotEmptyMultipartList, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {

        return files != null && !files.isEmpty() && files.stream().allMatch(file -> file.getSize() > 0);
    }
}
