package com.nowakArtur97.myMoments.userService.feature.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, BasicUserValidationConstraints.class})
public interface UserValidationGroupSequence {
}
