package com.mohamedgara.ai_teaching_platform.exercises.services;


import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.AI.services.PromptBuilderService;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRequestMapper exerciseRequestMapper;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final PromptBuilderService promptBuilderService;
    private final ExerciseGeneratorService exerciseGeneratorService;

    public void createExercise(CreateExerciseRequest createExerciseRequest){
        Exercise exercise = exerciseRequestMapper.toExercise(createExerciseRequest);
        JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
        System.out.println("this is the mapped exercise: " + result.toString() );
    }

}
