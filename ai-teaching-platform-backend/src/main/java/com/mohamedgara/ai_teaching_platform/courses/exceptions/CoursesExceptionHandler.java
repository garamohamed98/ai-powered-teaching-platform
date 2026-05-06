package com.mohamedgara.ai_teaching_platform.courses.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CoursesExceptionHandler {
    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleServiceNotFound(ServiceNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("SERVICE_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
