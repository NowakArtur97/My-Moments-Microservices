package com.nowakArtur97.myMoments.postService.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Schema(description = "Details about the Error")
public class ErrorResponse {

    @Schema(accessMode = READ_ONLY, description = "Error's time")
    private final LocalDateTime timestamp;

    @Schema(accessMode = READ_ONLY, description = "Error's status")
    private final int status;

    @Schema(accessMode = READ_ONLY, description = "Details about the cause of the error")
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

        return getStatus() == that.getStatus() && Objects.equals(getTimestamp(), that.getTimestamp()) && Objects.equals(getErrors(), that.getErrors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getStatus(), getErrors());
    }
}
