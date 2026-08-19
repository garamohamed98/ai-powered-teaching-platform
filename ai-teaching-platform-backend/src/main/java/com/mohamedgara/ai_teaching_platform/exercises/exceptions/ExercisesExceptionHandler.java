package com.mohamedgara.ai_teaching_platform.exercises.exceptions;

import com.mohamedgara.ai_teaching_platform.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExercisesExceptionHandler {
    @ExceptionHandler(LessonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(LessonNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("LESSON_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExerciseNotFound(ExerciseNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("EXERCISE_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(NoLessonReferenceException.class)
    public ResponseEntity<ErrorResponse> handleNoLessonReferenceException(NoLessonReferenceException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("NO_REFERENCE_CONTENT")
                .message(exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ExerciseGenerationException.class)
    public ResponseEntity<ErrorResponse> handleExerciseGenerationException(ExerciseGenerationException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("EXERCISE_GENERATION_FAILED")
                .message(exception.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ExerciseAttemptNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExerciseAttemptNotFoundException(ExerciseAttemptNotFoundException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("EXERCISE_ATTEMPT_NOT_FOUND")
                .message(exception.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InvalidAttemptTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAttemptTypeException(InvalidAttemptTypeException exception){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INVALID_EXERCISE_TYPE")
                .message("Invalid exercise type")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InvalidExerciseContentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidExerciseContentException(InvalidExerciseContentException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INVALID_EXERCISE_CONTENT")
                .message("Something went wrong while loading this exercise. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
