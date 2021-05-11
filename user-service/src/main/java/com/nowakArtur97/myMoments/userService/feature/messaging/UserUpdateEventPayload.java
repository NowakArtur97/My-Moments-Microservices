package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@ToString
public class UserUpdateEventPayload implements Serializable {

    private final String previousUsername;

    private final String newUsername;
}
