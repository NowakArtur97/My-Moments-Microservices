package com.nowakArtur97.myMoments.commentService.advice;

import com.nowakArtur97.myMoments.commentService.exception.ForbiddenException;
import com.nowakArtur97.myMoments.commentService.exception.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
class GlobalRestControllerAdvice {

    @ExceptionHandler({WebExchangeBindException.class})
    ResponseEntity<Object> handleWebExchangeBindException(WebExchangeBindException exception) {

        List<String> errors = exception.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
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
