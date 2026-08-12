package com.mohamedgara.ai_teaching_platform.exercises;

import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.GenerateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseSummaryResponse;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<CreateExerciseResponse> createExercise(
            @RequestBody @Valid CreateExerciseRequest createExerciseRequest
            ){

        CreateExerciseResponse response = exerciseService
                .createExercise(createExerciseRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("course/{courseId}")
    public ResponseEntity<List<ExerciseSummaryResponse>> getExercises(
            @PathVariable UUID courseId
    ){
        List<ExerciseSummaryResponse> response = exerciseService.getExercises(courseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable UUID id){
        ExerciseResponse response = exerciseService.getExercise(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseById(@PathVariable UUID id){
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExerciseResponse> correctExercise(@PathVariable UUID id){
        ExerciseResponse response = exerciseService.correctExercise(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<ExerciseResponse> generateExercise(
            @RequestBody @Valid GenerateExerciseRequest generateExerciseRequest
    ){
        ExerciseResponse response = exerciseService.generateExercise(generateExerciseRequest);
        return ResponseEntity.ok(response);
    }
}
