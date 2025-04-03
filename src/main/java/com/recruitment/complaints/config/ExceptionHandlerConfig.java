package com.recruitment.complaints.config;

import com.recruitment.complaints.exception.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class ExceptionHandlerConfig {

    @Bean
    public HandlerExceptionResolver customExceptionResolver() {
        return new AbstractHandlerExceptionResolver() {
            @Override
            protected ModelAndView doResolveException(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Object handler,
                    Exception ex) {

                try {
                    log.error("Global exception caught: {}", ex.getMessage(), ex);
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    if (ex instanceof HttpMessageNotReadableException) {
                        status = HttpStatus.BAD_REQUEST;
                    }
                    ApiError apiError = ApiError.builder()
                            .message("An error occurred while processing your request")
                            .errorCode(status.toString())
                            .timestamp(LocalDateTime.now())
                            .errors(new ArrayList<>(List.of(ex.getMessage())))
                            .build();
                    response.setStatus(status.value());
                    response.setContentType("application/json");

                    log.debug("Sending error response: {}", apiError);

                    return new ModelAndView();
                } catch (Exception handlerException) {
                    log.error("Exception handling exception", handlerException);
                    return null;
                }
            }
        };
    }
}
