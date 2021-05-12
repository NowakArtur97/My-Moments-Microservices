package com.nowakArtur97.myMoments.commentService.feature.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
class UserUpdateEventPayload {

    private final String previousUsername;

    private final String newUsername;
}
