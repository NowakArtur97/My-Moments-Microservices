package com.nowakArtur97.myMoments.postService.advice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@ApiModel(description = "Details about the Error")
public class ErrorResponse {

    @ApiModelProperty(notes = "Error's time")
    private final LocalDateTime timestamp;

    @ApiModelProperty(notes = "Error's status")
    private final int status;

    @ApiModelProperty(notes = "Details about the cause of the error")
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
