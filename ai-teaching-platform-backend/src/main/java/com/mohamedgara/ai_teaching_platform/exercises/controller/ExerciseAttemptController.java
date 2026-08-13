package com.mohamedgara.ai_teaching_platform.exercises.controller;

import com.mohamedgara.ai_teaching_platform.exercises.dto.response.StartExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exercise-attempt")
@RequiredArgsConstructor
public class ExerciseAttemptController {

    private final ExerciseAttemptService exerciseAttemptService;

    @PostMapping("/{exerciseId}/attempt")
    public ResponseEntity<StartExerciseResponse> startExerciseAttempt(
            @PathVariable UUID exerciseId
            ){
        StartExerciseResponse response = exerciseAttemptService.startExerciseAttempt(exerciseId);
        return ResponseEntity.ok(response);
    }
}
