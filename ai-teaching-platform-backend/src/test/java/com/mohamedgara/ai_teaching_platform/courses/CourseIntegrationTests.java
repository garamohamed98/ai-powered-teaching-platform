package com.mohamedgara.ai_teaching_platform.courses;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.CreateCourseRequest;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
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
import com.mohamedgara.ai_teaching_platform.courses.dto.request.CourseContentUpdateRequest;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class CourseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCourse_shouldReturn201AndPersistCourse() throws Exception{
        CreateCourseRequest request = new CreateCourseRequest("Intro to Spring Boot");

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Intro to Spring Boot"));

        assert courseRepository.count() == 1;

    }

    @Test
    void createCourse_shouldReturn400WhenTitleIsBlank() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest("");
        System.out.println(objectMapper.writeValueAsString(request));

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assert courseRepository.count() == 0;
    }

    @Test
    void createCourse_shouldReturn400WhenTitleTooShort() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest("ab");

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCourseList_shouldReturnAllCourses() throws Exception {
        courseRepository.save(Course.builder().title("Course A").build());
        courseRepository.save(Course.builder().title("Course B").build());

        mockMvc.perform(get("/api/course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Course A", "Course B")));
    }

    @Test
    void getCourse_shouldReturnCourseWhenFound() throws Exception {
        Course saved = courseRepository.save(Course.builder().title("Java Basics").build());

        mockMvc.perform(get("/api/course/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.title").value("Java Basics"));
    }

    @Test
    void getCourse_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/course/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("SERVICE_NOT_FOUND"));
    }

    @Test
    void deleteCourse_shouldRemoveCourseWhenFound() throws Exception {
        Course saved = courseRepository.save(Course.builder().title("To Delete").build());

        mockMvc.perform(delete("/api/course/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assert courseRepository.count() == 0;
    }

    @Test
    void deleteCourse_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/api/course/{id}", randomId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCourseContent_shouldUpdateContentWhenFound() throws Exception {
        Course saved = courseRepository.save(Course.builder().title("Spring Data JPA").build());
        CourseContentUpdateRequest request = new CourseContentUpdateRequest("New lesson content here");

        mockMvc.perform(patch("/api/course/{courseId}/content", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New lesson content here"));

        Course updated = courseRepository.findById(saved.getId()).orElseThrow();
        assert updated.getContent().equals("New lesson content here");
    }

    @Test
    void updateCourseContent_shouldReturn404WhenNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        CourseContentUpdateRequest request = new CourseContentUpdateRequest("Some content");

        mockMvc.perform(patch("/api/course/{courseId}/content", randomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
