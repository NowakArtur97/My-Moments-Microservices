package com.nowakArtur97.myMoments.postService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {

        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {

        super(resourceType + " with id: '" + id + "' not found.");
    }
}
