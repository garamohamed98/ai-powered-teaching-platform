package com.mohamedgara.ai_teaching_platform.AI.exceptions;

import com.mohamedgara.ai_teaching_platform.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AIExceptionHandler {

    @ExceptionHandler(GeminiParseFailException.class)
    public ResponseEntity<ErrorResponse> handleGeminiParseFail(GeminiParseFailException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(AiServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceUnavailable(AiServiceUnavailableException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("SERVICE_UNAVAILABLE")
                .message(exception.getMessage())
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(AiRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(AiRequestException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("BAD_REQUEST")
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(AiQuotaExceededException.class)
    public ResponseEntity<ErrorResponse> handleQuotaExceeded(AiQuotaExceededException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("PAYMENT_REQUIRED")
                .message(exception.getMessage())
                .status(HttpStatus.PAYMENT_REQUIRED)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(AIRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(AIRateLimitException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("TOO_MANY_REQUESTS")
                .message(exception.getMessage())
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
