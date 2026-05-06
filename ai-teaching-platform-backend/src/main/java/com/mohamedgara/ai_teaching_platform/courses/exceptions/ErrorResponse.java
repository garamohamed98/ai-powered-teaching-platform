package com.mohamedgara.ai_teaching_platform.courses.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;

}