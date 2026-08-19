package com.mohamedgara.ai_teaching_platform.exercises.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.StartExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.exercisestartcontent.MultipleChoiceStartContent;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseAttemptServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseResponseMapper exerciseResponseMapper;

    @Mock
    private ExerciseAttemptRepository exerciseAttemptRepository;

    @InjectMocks
    private ExerciseAttemptService exerciseAttemptService;

    private ObjectMapper objectMapper;

    private UUID exerciseId;
    private UUID exerciseAttemptId;
    private JsonNode contentJson;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        exerciseId = UUID.randomUUID();
        exerciseAttemptId = UUID.randomUUID();
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("question", "What is 2+2?");
        contentNode.put("correctAnswer", "4");
        contentJson = contentNode;
        exercise = Exercise.builder()
                .id(exerciseId)
                .lessonIdList(List.of(UUID.randomUUID()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
    }

    @Test
    void startExerciseAttempt_shouldCreateAttemptAndReturnResponse_whenExerciseExists() {
        StartExerciseResponse expectedResponse = new StartExerciseResponse(
                exerciseId, exerciseAttemptId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new MultipleChoiceStartContent("What is 2+2?", List.of("4", "5")));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseAttemptRepository.save(any(ExerciseAttempt.class))).thenAnswer(invocation -> {
            ExerciseAttempt attempt = invocation.getArgument(0);
            attempt.setId(exerciseAttemptId);
            return attempt;
        });
        when(exerciseResponseMapper.toStartExerciseResponse(exercise, exerciseAttemptId)).thenReturn(expectedResponse);

        StartExerciseResponse response = exerciseAttemptService.startExerciseAttempt(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(exerciseAttemptId, response.exerciseAttemptId());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseAttemptRepository).save(any(ExerciseAttempt.class));
        verify(exerciseResponseMapper).toStartExerciseResponse(exercise, exerciseAttemptId);
    }


    @Test
    void startExerciseAttempt_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseAttemptService.startExerciseAttempt(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseAttemptRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toStartExerciseResponse(any(), any());
    }
}
