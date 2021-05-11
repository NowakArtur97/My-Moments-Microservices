package com.nowakArtur97.myMoments.postService.feature.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
class UserUpdateEventPayload implements Serializable {

    private String previousUsername;

    private String newUsername;
}
