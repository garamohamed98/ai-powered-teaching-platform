package com.mohamedgara.ai_teaching_platform.exercises.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findExercises_shouldReturnExercisesWhenLessonIdListMatches(){
        UUID lessonIdOne = UUID.randomUUID();
        UUID lessonIdTwo = UUID.randomUUID();
        Exercise savedExerciseOne = exerciseRepository.save(buildExercise(lessonIdOne, "Exercise One"));
        Exercise savedExerciseTwo = exerciseRepository.save(buildExercise(lessonIdTwo, "Exercise Two"));

        List<Exercise> result = exerciseRepository.findExercises(List.of(lessonIdOne, lessonIdTwo));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(exercise -> exercise.getId().equals(savedExerciseOne.getId())));
        assertTrue(result.stream().anyMatch(exercise -> exercise.getId().equals(savedExerciseTwo.getId())));
    }

    @Test
    void findExercises_shouldReturnOnlyExercisesOfGivenLessonIdList(){
        UUID lessonIdOne = UUID.randomUUID();
        UUID lessonIdTwo = UUID.randomUUID();
        Exercise savedExerciseOne = exerciseRepository.save(buildExercise(lessonIdOne, "Exercise One"));
        Exercise savedExerciseTwo = exerciseRepository.save(buildExercise(lessonIdTwo, "Exercise Two"));

        List<Exercise> result = exerciseRepository.findExercises(List.of(lessonIdOne));

        assertEquals(1, result.size());
        assertEquals(savedExerciseOne.getId(), result.get(0).getId());
        assertTrue(result.stream().noneMatch(exercise -> exercise.getId().equals(savedExerciseTwo.getId())));
    }
    @Test
    void findExercises_shouldNotReturnDuplicates_whenExerciseMatchesMultipleQueriedLessons() {
        UUID lessonIdOne = UUID.randomUUID();
        UUID lessonIdTwo = UUID.randomUUID();
        Exercise multiLessonExercise = exerciseRepository.save(
                buildExercise(List.of(lessonIdOne, lessonIdTwo), "Multi-Lesson Exercise")
        );

        List<Exercise> result = exerciseRepository.findExercises(List.of(lessonIdOne, lessonIdTwo));

        assertEquals(1, result.size());
        assertEquals(multiLessonExercise.getId(), result.get(0).getId());
    }

    @Test
    void findExercises_shouldReturnEmptyListWhenNoExercisesExistForLessonIdList() {
        UUID randomLessonId = UUID.randomUUID();

        List<Exercise> result = exerciseRepository.findExercises(List.of(randomLessonId));

        assertTrue(result.isEmpty());
    }

    @Test
    void findExercises_shouldReturnEmptyListWhenLessonIdListIsEmpty() {
        List<Exercise> result = exerciseRepository.findExercises(List.of());

        assertTrue(result.isEmpty());
    }

    private Exercise buildExercise(UUID lessonId, String title) {
        return buildExercise(List.of(lessonId), title);
    }

    private Exercise buildExercise(List<UUID> lessonIdlist, String title) {
        return Exercise.builder()
                .lessonIdList(lessonIdlist)
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title(title)
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build();
    }
}
