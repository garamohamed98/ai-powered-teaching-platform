package com.mohamedgara.ai_teaching_platform.exercises;

import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    private ResponseEntity<CreateExerciseResponse> createExercise(
            @RequestBody @Valid CreateExerciseRequest createExerciseRequest
            ){

        CreateExerciseResponse response = exerciseService
                .createExercise(createExerciseRequest);

        return ResponseEntity.ok(response);
    }
}
