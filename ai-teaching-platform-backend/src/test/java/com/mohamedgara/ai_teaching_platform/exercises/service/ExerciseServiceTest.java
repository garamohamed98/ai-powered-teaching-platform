package com.mohamedgara.ai_teaching_platform.exercises.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mohamedgara.ai_teaching_platform.AI.services.ExerciseGeneratorService;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.CreateExerciseRequest;
import com.mohamedgara.ai_teaching_platform.exercises.dto.request.content.MultipleChoiceContent;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.CreateExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseResponse;
import com.mohamedgara.ai_teaching_platform.exercises.dto.response.ExerciseSummaryResponse;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.enums.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.CourseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.exceptions.ExerciseNotFoundException;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseRequestMapper;
import com.mohamedgara.ai_teaching_platform.exercises.mappers.ExerciseResponseMapper;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import com.mohamedgara.ai_teaching_platform.exercises.services.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseRequestMapper exerciseRequestMapper;

    @Mock
    private ExerciseResponseMapper exerciseResponseMapper;

    @Mock
    private ExerciseGeneratorService exerciseGeneratorService;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private ExerciseService exerciseService;

    private ObjectMapper objectMapper;

    private UUID lessonId;
    private UUID exerciseId;
    private UUID courseId;
    private JsonNode contentJson;
    private Exercise exercise;
    private CreateExerciseRequest createRequest;
    private CreateExerciseRequest createRequestWithAnswers;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        lessonId = UUID.randomUUID();
        exerciseId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("question", "What is 2+2?");
        contentNode.put("correctAnswer", "4");
        contentJson = contentNode;
        exercise = Exercise.builder()
                .id(exerciseId)
                .lessonId(lessonId)
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(contentJson)
                .build();
        MultipleChoiceContent content = new MultipleChoiceContent("What is 2+2?", List.of("4", "5"), "4");
        createRequest = new CreateExerciseRequest(
                lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer", false, content);
        createRequestWithAnswers = new CreateExerciseRequest(
                lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer", true, content);
    }

    @Test
    void createExercise_shouldThrowCourseNotFoundException_whenLessonDoesNotExist() {
        when(lessonService.lessonExists(lessonId)).thenReturn(false);

        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> exerciseService.createExercise(createRequest));

        assertEquals("Course not Found", exception.getMessage());

        verify(lessonService).lessonExists(lessonId);
        verify(exerciseRequestMapper, never()).toExercise(any());
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toCreateExerciseResponse(any());
    }

    @Test
    void createExercise_shouldCreateExerciseAndReturnResponse_whenCorrectAnswersIsFalse() {
        CreateExerciseResponse expectedResponse = new CreateExerciseResponse(
                exerciseId, lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(lessonService.lessonExists(lessonId)).thenReturn(true);
        when(exerciseRequestMapper.toExercise(createRequest)).thenReturn(exercise);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toCreateExerciseResponse(exercise)).thenReturn(expectedResponse);

        CreateExerciseResponse response = exerciseService.createExercise(createRequest);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonId, response.lessonId());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(lessonService).lessonExists(lessonId);
        verify(exerciseRequestMapper).toExercise(createRequest);
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toCreateExerciseResponse(exercise);
    }

    @Test
    void createExercise_shouldGenerateAndSetAnswers_whenCorrectAnswersIsTrue() {
        ObjectNode generatedContentNode = objectMapper.createObjectNode();
        generatedContentNode.put("question", "What is 2+2?");
        generatedContentNode.put("correctAnswer", "4");
        JsonNode generatedContent = generatedContentNode;

        CreateExerciseResponse expectedResponse = new CreateExerciseResponse(
                exerciseId, lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(lessonService.lessonExists(lessonId)).thenReturn(true);
        when(exerciseRequestMapper.toExercise(createRequestWithAnswers)).thenReturn(exercise);
        when(exerciseGeneratorService.generateExerciseAnswer(contentJson)).thenReturn(generatedContent);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toCreateExerciseResponse(exercise)).thenReturn(expectedResponse);

        CreateExerciseResponse response = exerciseService.createExercise(createRequestWithAnswers);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonId, response.lessonId());
        assertEquals(generatedContent, exercise.getContent());

        verify(lessonService).lessonExists(lessonId);
        verify(exerciseRequestMapper).toExercise(createRequestWithAnswers);
        verify(exerciseGeneratorService).generateExerciseAnswer(contentJson);
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toCreateExerciseResponse(exercise);
    }

    @Test
    void getExercises_shouldReturnExerciseListResponse_whenCourseHasLessonsAndExercisesExist() {
        Map<UUID, String> lessonsIdAndTitleList = Map.of(lessonId, "Lesson Title");
        List<Exercise> exerciseList = List.of(exercise);
        ExerciseSummaryResponse expectedResponse = new ExerciseSummaryResponse(
                exerciseId, "Addition", ExerciseType.MULTIPLE_CHOICE,
                new ExerciseSummaryResponse.LessonSummaryResponse(lessonId, "Lesson Title"));
        List<ExerciseSummaryResponse> expectedList = List.of(expectedResponse);

        when(lessonService.getLessonIdAndTitleListByCourseId(courseId)).thenReturn(lessonsIdAndTitleList);
        when(exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()))).thenReturn(exerciseList);
        when(exerciseResponseMapper.toExerciseListResponse(exerciseList, lessonsIdAndTitleList)).thenReturn(expectedList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exerciseId, result.get(0).id());
        assertEquals("Addition", result.get(0).title());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, result.get(0).type());
        assertEquals(lessonId, result.get(0).lesson().id());
        assertEquals("Lesson Title", result.get(0).lesson().title());

        verify(lessonService).getLessonIdAndTitleListByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(exerciseList, lessonsIdAndTitleList);
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseHasNoLessons() {
        Map<UUID, String> emptyMap = Map.of();
        List<ExerciseSummaryResponse> emptyList = List.of();

        when(lessonService.getLessonIdAndTitleListByCourseId(courseId)).thenReturn(emptyMap);
        when(exerciseRepository.findExercises(new ArrayList<>(emptyMap.keySet()))).thenReturn(List.of());
        when(exerciseResponseMapper.toExerciseListResponse(List.of(), emptyMap)).thenReturn(emptyList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lessonService).getLessonIdAndTitleListByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(emptyMap.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(List.of(), emptyMap);
    }

    @Test
    void getExercises_shouldReturnEmptyList_whenCourseHasLessonsButNoExercises() {
        Map<UUID, String> lessonsIdAndTitleList = Map.of(lessonId, "Lesson Title");
        List<ExerciseSummaryResponse> emptyList = List.of();

        when(lessonService.getLessonIdAndTitleListByCourseId(courseId)).thenReturn(lessonsIdAndTitleList);
        when(exerciseRepository.findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()))).thenReturn(List.of());
        when(exerciseResponseMapper.toExerciseListResponse(List.of(), lessonsIdAndTitleList)).thenReturn(emptyList);

        List<ExerciseSummaryResponse> result = exerciseService.getExercises(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lessonService).getLessonIdAndTitleListByCourseId(courseId);
        verify(exerciseRepository).findExercises(new ArrayList<>(lessonsIdAndTitleList.keySet()));
        verify(exerciseResponseMapper).toExerciseListResponse(List.of(), lessonsIdAndTitleList);
    }

    @Test
    void getExercise_shouldReturnExerciseResponse_whenExerciseExists() {
        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseResponseMapper.toExerciseResponse(exercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.getExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonId, response.lessonId());
        assertEquals(ExerciseType.MULTIPLE_CHOICE, response.type());
        assertEquals("Addition", response.title());
        assertEquals("Choose the correct answer", response.instructions());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseResponseMapper).toExerciseResponse(exercise);
    }

    @Test
    void getExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseResponseMapper, never()).toExerciseResponse(any());
    }

    @Test
    void getExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }

    @Test
    void deleteExercise_shouldDeleteExercise_whenExerciseExists() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));

        exerciseService.deleteExercise(exerciseId);

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository).delete(exercise);
    }

    @Test
    void deleteExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.deleteExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseRepository, never()).delete(any());
    }

    @Test
    void deleteExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.deleteExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }

    @Test
    void correctExercise_shouldGenerateAnswersSetContentAndReturnResponse_whenExerciseExists() {
        ObjectNode generatedContentNode = objectMapper.createObjectNode();
        generatedContentNode.put("question", "What is 2+2?");
        generatedContentNode.put("correctAnswer", "4");
        JsonNode generatedContent = generatedContentNode;

        ExerciseResponse expectedResponse = new ExerciseResponse(
                exerciseId, lessonId, ExerciseType.MULTIPLE_CHOICE, "Addition", "Choose the correct answer",
                new com.mohamedgara.ai_teaching_platform.exercises.dto.response.content.MultipleChoiceContent(
                        "What is 2+2?", List.of("4", "5"), "4"));

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(exercise));
        when(exerciseGeneratorService.generateExerciseAnswer(contentJson)).thenReturn(generatedContent);
        when(exerciseRepository.save(exercise)).thenReturn(exercise);
        when(exerciseResponseMapper.toExerciseResponse(exercise)).thenReturn(expectedResponse);

        ExerciseResponse response = exerciseService.correctExercise(exerciseId);

        assertNotNull(response);
        assertEquals(exerciseId, response.id());
        assertEquals(lessonId, response.lessonId());
        assertEquals(generatedContent, exercise.getContent());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseGeneratorService).generateExerciseAnswer(contentJson);
        verify(exerciseRepository).save(exercise);
        verify(exerciseResponseMapper).toExerciseResponse(exercise);
    }

    @Test
    void correctExercise_shouldThrowExerciseNotFoundException_whenExerciseDoesNotExist() {
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.correctExercise(exerciseId));

        assertEquals("Exercise not Found", exception.getMessage());

        verify(exerciseRepository).findById(exerciseId);
        verify(exerciseGeneratorService, never()).generateExerciseAnswer(any());
        verify(exerciseRepository, never()).save(any());
        verify(exerciseResponseMapper, never()).toExerciseResponse(any());
    }

    @Test
    void correctExercise_shouldThrowExerciseNotFoundExceptionForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.correctExercise(invalidId));

        verify(exerciseRepository).findById(invalidId);
    }
}
