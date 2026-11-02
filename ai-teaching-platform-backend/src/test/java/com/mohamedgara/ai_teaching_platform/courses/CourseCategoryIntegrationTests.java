package com.mohamedgara.ai_teaching_platform.courses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.repository.CourseCategoryRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Transactional
public class CourseCategoryIntegrationTests {

    private static final String OWNER_SUB = "auth0|owner-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestPostProcessor asOwner(){
        return jwt().jwt(j->j.subject(OWNER_SUB));
    }

    @Test
    void getCourseCategoryList_shouldReturnCourseCategoryList_whenCourseCategoriesExists() throws Exception{
        //Arrange
        CourseCategory courseCategory1 = courseCategoryRepository.save(CourseCategory.builder()
                        .name("Java")
                        .slug("java")
                        .displayOrder(1)
                .build());

        CourseCategory courseCategory2 = courseCategoryRepository.save(CourseCategory.builder()
                .name("React")
                .slug("react")
                .displayOrder(2)
                .build());

        //Act & Assert

        mockMvc.perform(get("/api/course-category")
                .with(asOwner())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(jsonPath("$[*].id",containsInAnyOrder(courseCategory1.getId().toString(),courseCategory2.getId().toString())))
                .andExpect(jsonPath("$[*].name",containsInAnyOrder(courseCategory1.getName(),courseCategory2.getName())))
                .andExpect(jsonPath("$[*].slug",containsInAnyOrder(courseCategory1.getSlug(),courseCategory2.getSlug())))
                .andExpect(jsonPath("$[*].displayOrder",containsInAnyOrder(courseCategory1.getDisplayOrder(),courseCategory2.getDisplayOrder())));
    }

    @Test
    void getCourseCategoryList_shouldReturnEmptyList_whenCourseCategoriesDoNotExist() throws Exception{

        //Act & Assert
        mockMvc.perform(get("/api/course-category")
                        .with(asOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
