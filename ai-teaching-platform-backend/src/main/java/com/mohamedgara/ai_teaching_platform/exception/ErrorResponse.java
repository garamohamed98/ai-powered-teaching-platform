package com.mohamedgara.ai_teaching_platform.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private List<Map<String,String>> errors;
}