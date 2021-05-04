package com.nowakArtur97.myMoments.postService.common.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;

    private final int status;

    private final List<String> errors;

    public ErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ErrorResponse)) return false;

        ErrorResponse that = (ErrorResponse) o;

        return getStatus() == that.getStatus() &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getErrors(), that.getErrors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getStatus(), getErrors());
    }
}
