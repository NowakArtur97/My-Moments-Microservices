package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class UserUpdateEventPayload {

    private final String previousUsername;

    private final String newUsername;
}
