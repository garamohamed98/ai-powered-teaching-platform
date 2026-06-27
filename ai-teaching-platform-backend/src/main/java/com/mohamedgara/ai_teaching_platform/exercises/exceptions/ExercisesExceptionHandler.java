package com.mohamedgara.ai_teaching_platform.exercises.exceptions;

import com.mohamedgara.ai_teaching_platform.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExercisesExceptionHandler {
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(CourseNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("COURSE_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    public ResponseEntity<ErrorResponse> handleExerciseNotFound(ExerciseNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("EXERCISE_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
