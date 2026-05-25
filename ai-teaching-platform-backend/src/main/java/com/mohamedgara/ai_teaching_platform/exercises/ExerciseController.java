package com.mohamedgara.ai_teaching_platform.exercises;

import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
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

    @PostMapping
    private ResponseEntity<CreateExerciseRequest> createExercise(
            @RequestBody @Valid CreateExerciseRequest createExerciseRequest
            ){
        System.out.println(createExerciseRequest);
        return ResponseEntity.ok(createExerciseRequest);
    }
}
