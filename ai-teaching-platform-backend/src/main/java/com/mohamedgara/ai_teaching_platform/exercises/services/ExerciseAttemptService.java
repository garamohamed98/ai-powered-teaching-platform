package com.mohamedgara.ai_teaching_platform.exercises.services;

import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.StartExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExerciseAttemptService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final ExerciseAttemptRepository exerciseAttemptRepository;

    public StartExerciseResponse startExerciseAttempt(UUID exerciseId){
        Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow(()-> new ExerciseNotFoundException());

        ExerciseAttempt exerciseAttempt = ExerciseAttempt
                .builder()
                .exerciseId(exerciseId)
                .build();

        ExerciseAttempt savedExerciseAttempt = exerciseAttemptRepository.save(exerciseAttempt);

        return exerciseResponseMapper.toExerciseAttemptResponse(exercise,exerciseAttempt.getId());
    }
}
