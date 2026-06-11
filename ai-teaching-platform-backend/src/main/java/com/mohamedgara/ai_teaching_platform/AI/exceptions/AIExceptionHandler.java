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
}
