package com.nowakArtur97.myMoments.userService.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowakArtur97.myMoments.userService.common.model.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);

        } catch (AuthenticationException exception) {

            setErrorResponse(HttpStatus.UNAUTHORIZED, response, exception);

        } catch (RuntimeException exception) {

            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, exception);
        }
    }

    private void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable exception) {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), List.of(exception.getMessage()));

        try {

            String json = objectMapper.writeValueAsString(errorResponse);

            response.getWriter().write(json);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}

