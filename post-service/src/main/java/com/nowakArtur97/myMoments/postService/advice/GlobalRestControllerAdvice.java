package com.nowakArtur97.myMoments.postService.advice;


import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.postService.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class})
    ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {

        List<String> errors = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), errors);

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                List.of(exception.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ForbiddenException.class})
    ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception) {

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
                List.of(exception.getMessage()));

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
