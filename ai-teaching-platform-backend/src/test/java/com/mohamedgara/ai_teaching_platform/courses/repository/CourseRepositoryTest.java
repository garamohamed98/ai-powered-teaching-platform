package com.mohamedgara.ai_teaching_platform.courses.repository;

import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.CourseResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    String user1Id = "user1";
    String user2Id = "user2";



    @Test
    void findCourseByOwnerId_shouldReturnOnlyOwnersCourses_whenCoursesExists(){
        //Assert
        CourseCategory courseCategory = courseCategoryRepository.save(
                CourseCategory.builder()
                        .name("java")
                        .slug("java")
                        .displayOrder(1)
                        .build()
        );
        Course savedCourse1 = courseRepository.save(
                Course.builder()
                        .title("course1")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );
        Course savedCourse2 = courseRepository.save(
                Course.builder()
                        .title("course2")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );

        Course savedCourse3 = courseRepository.save(
                Course.builder()
                        .title("course3")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user2Id)
                        .build()
        );

        List<Course> result = courseRepository.findCourseByOwnerId(user1Id);

        Assertions.assertThat(result)
                .isNotNull()
                .hasSize(2)
                .extracting(Course::getTitle)
                .containsExactlyInAnyOrder("course1", "course2");
    }

    @Test
    void findCourseByOwnerId_shouldReturnEmptyList_whenUserHasNoCourses() {
        // Arrange (Optional: you can add a course for another user to ensure it doesn't accidentally leak)

        // Act
        List<Course> result = courseRepository.findCourseByOwnerId("non-existent-user-id");

        // Assert
        Assertions.assertThat(result)
                .isNotNull()
                .isEmpty();
    }

}
