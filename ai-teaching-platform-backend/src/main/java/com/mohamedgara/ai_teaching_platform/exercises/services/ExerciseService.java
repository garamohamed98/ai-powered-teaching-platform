package com.mohamedgara.ai_teaching_platform.exercises.services;


import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.CourseService;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRequestMapper exerciseRequestMapper;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final ExerciseGeneratorService exerciseGeneratorService;
    private final CourseService courseService;


    public CreateExerciseResponse createExercise(CreateExerciseRequest createExerciseRequest){
        if(!courseService.courseExists(createExerciseRequest.courseId())){
            throw new CourseNotFoundException();
        }
        System.out.println("DTO: " + createExerciseRequest.content());
        Exercise exercise = exerciseRequestMapper.toExercise(createExerciseRequest);
        System.out.println("mapper: " + exercise);

        boolean correctAnswers = createExerciseRequest.correctAnswers();

        if(correctAnswers){
            JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
            exercise.setContent(result);
        }
        System.out.println("Exercise: " + exercise.getContent());
        Exercise savedExercise = exerciseRepository.save(exercise);
        System.out.println("Saved exercise: " + savedExercise.getContent());

        return exerciseResponseMapper.toCreateExerciseResponse(savedExercise);
    }

}
