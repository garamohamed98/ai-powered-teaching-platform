package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseRepository;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class LessonIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updateLessonTitle_shouldUpdateContentWhenFound() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Old Lesson Title").course(savedCourse).build());
        LessonTitleUpdateRequest request = new LessonTitleUpdateRequest("New Lesson Title");

        mockMvc.perform(patch("/api/lesson/{lessonId}/title", savedLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Lesson Title"))
                .andExpect(jsonPath("$.id").value(savedLesson.getId().toString()))
                .andExpect(jsonPath("$.course_id").value(savedCourse.getId().toString()));

        Lesson updated = lessonRepository.findById(savedLesson.getId()).orElseThrow();
        assert updated.getTitle().equals("New Lesson Title");
    }

    @Test
    void updateLessonTitle_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        LessonTitleUpdateRequest request = new LessonTitleUpdateRequest("Some title");

        mockMvc.perform(patch("/api/lesson/{lessonId}/title", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateLessonTitle_shouldReturn400WhenTitleIsBlank() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Old Lesson Title").course(savedCourse).build());
        LessonTitleUpdateRequest request = new LessonTitleUpdateRequest("");

        mockMvc.perform(patch("/api/lesson/{lessonId}/title", savedLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLessonTitle_shouldReturn400WhenTitleTooShort() throws Exception {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLesson = lessonRepository.save(Lesson.builder().title("Old Lesson Title").course(savedCourse).build());
        LessonTitleUpdateRequest request = new LessonTitleUpdateRequest("ab");

        mockMvc.perform(patch("/api/lesson/{lessonId}/title", savedLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
