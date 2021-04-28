package com.nowakArtur97.myMoments.postService.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowakArtur97.myMoments.postService.common.model.ErrorResponse;
import com.nowakArtur97.myMoments.postService.exception.JwtTokenMissingException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(-2)
class RestWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable exception) {

        if (exception instanceof UsernameNotFoundException) {

            return setErrorResponse(HttpStatus.UNAUTHORIZED, serverWebExchange, exception);

        } else if (exception instanceof JwtTokenMissingException) {

            return setErrorResponse(HttpStatus.UNAUTHORIZED, serverWebExchange, exception);

        } else if (exception instanceof JwtException) {

            return setErrorResponse(HttpStatus.BAD_REQUEST, serverWebExchange, exception);

        } else {

            return setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, serverWebExchange, exception);
        }
    }

    private Mono<Void> setErrorResponse(HttpStatus status, ServerWebExchange serverWebExchange, Throwable exception) {

        ServerHttpResponse response = serverWebExchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), List.of(exception.getMessage()));

        try {

            String json = objectMapper.writeValueAsString(errorResponse);

            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Flux.just(dataBuffer));

        } catch (IOException e) {

            e.printStackTrace();

            return Mono.error(exception);
        }
    }
}
