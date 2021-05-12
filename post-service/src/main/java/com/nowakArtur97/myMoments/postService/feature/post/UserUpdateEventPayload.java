package com.nowakArtur97.myMoments.postService.feature.post;

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
