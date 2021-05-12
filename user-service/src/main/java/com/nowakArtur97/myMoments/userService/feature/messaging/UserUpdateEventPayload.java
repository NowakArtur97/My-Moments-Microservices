package com.nowakArtur97.myMoments.userService.feature.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
@ToString
public class UserUpdateEventPayload {

    private final String previousUsername;

    private final String newUsername;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof UserUpdateEventPayload)) return false;

        UserUpdateEventPayload that = (UserUpdateEventPayload) o;

        return Objects.equals(getPreviousUsername(), that.getPreviousUsername())
                && Objects.equals(getNewUsername(), that.getNewUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPreviousUsername(), getNewUsername());
    }
}
