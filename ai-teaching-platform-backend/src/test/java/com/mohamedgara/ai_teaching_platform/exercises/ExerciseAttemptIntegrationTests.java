package com.mohamedgara.ai_teaching_platform.exercises;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseAttempt;
import com.mohamedgara.ai_teaching_platform.exercises.entities.ExerciseType;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseAttemptRepository;
import com.mohamedgara.ai_teaching_platform.exercises.repositories.ExerciseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class ExerciseAttemptIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseAttemptRepository exerciseAttemptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void startExerciseAttempt_shouldCreateAttemptAndReturnResponseWhenExerciseExists() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Lesson Title").course(savedCourse).build());
        Exercise savedExercise = exerciseRepository.save(Exercise.builder()
                .lessonIdList(List.of(savedLesson.getId()))
                .type(ExerciseType.MULTIPLE_CHOICE)
                .title("Addition")
                .instructions("Choose the correct answer")
                .content(objectMapper.valueToTree(Map.of(
                        "question", "What is 2+2?",
                        "options", List.of("4", "5"),
                        "correctAnswer", "4")))
                .build());

        mockMvc.perform(post("/api/exercise-attempt/{exerciseId}/attempt", savedExercise.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExercise.getId().toString()))
                .andExpect(jsonPath("$.exercise_attempt_id").isNotEmpty())
                .andExpect(jsonPath("$.type").value("MULTIPLE_CHOICE"))
                .andExpect(jsonPath("$.title").value("Addition"))
                .andExpect(jsonPath("$.instructions").value("Choose the correct answer"))
                .andExpect(jsonPath("$.content.question").value("What is 2+2?"))
                .andExpect(jsonPath("$.content.options[0]").value("4"))
                .andExpect(jsonPath("$.content.options[1]").value("5"));

        assert exerciseAttemptRepository.count() == 1;
        ExerciseAttempt savedAttempt = exerciseAttemptRepository.findAll().get(0);
        assert savedAttempt.getExerciseId().equals(savedExercise.getId());
    }

    @Test
    void startExerciseAttempt_shouldReturn404WhenExerciseDoesNotExist() throws Exception {
        UUID randomExerciseId = UUID.randomUUID();

        mockMvc.perform(post("/api/exercise-attempt/{exerciseId}/attempt", randomExerciseId))
                .andExpect(status().isNotFound());
    }
}