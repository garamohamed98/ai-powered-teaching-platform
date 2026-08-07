package com.mohamedgara.ai_teaching_platform.exercises.services;


import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseSummaryResponse;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRequestMapper exerciseRequestMapper;
    private final ExerciseResponseMapper exerciseResponseMapper;
    private final ExerciseGeneratorService exerciseGeneratorService;
    private final LessonService lessonService;


    public CreateExerciseResponse createExercise(CreateExerciseRequest createExerciseRequest){
        if(!lessonService.lessonExists(createExerciseRequest.lessonId())){
            throw new CourseNotFoundException();
        }
        Exercise exercise = exerciseRequestMapper.toExercise(createExerciseRequest);

        boolean correctAnswers = createExerciseRequest.correctAnswers();

        if(correctAnswers){
            JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
            exercise.setContent(result);
        }
        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseResponseMapper.toCreateExerciseResponse(savedExercise);
    }

    public List<ExerciseSummaryResponse> getExercises(UUID courseId) {
        Map<UUID,String> lessonsIdAndTitleList = lessonService.getLessonIdAndTitleListByCourseId(courseId);
        List<Exercise> exerciseList = exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));

        return exerciseResponseMapper.toExerciseListResponse(exerciseList, lessonsIdAndTitleList);
    }

    public ExerciseResponse getExercise(UUID id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );
        return exerciseResponseMapper.toExerciseResponse(exercise);
    }

    public void deleteExercise(UUID id) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );
        exerciseRepository.delete(exercise);
    }

    public ExerciseResponse correctExercise(UUID id){
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(
                ()-> new ExerciseNotFoundException()
        );

        JsonNode result = exerciseGeneratorService.generateExerciseAnswer(exercise.getContent());
        exercise.setContent(result);
        Exercise savedExercise = exerciseRepository.save(exercise);

        return exerciseResponseMapper.toExerciseResponse(savedExercise);
    }
}
