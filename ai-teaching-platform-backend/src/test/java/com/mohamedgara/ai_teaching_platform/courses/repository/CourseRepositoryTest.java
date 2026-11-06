package com.mohamedgara.ai_teaching_platform.courses.repository;

import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseCategory;
import com.mohamedgara.ai_teaching_platform.courses.entity.CourseVisibility;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
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
        // Arrange

        // Act
        List<Course> result = courseRepository.findCourseByOwnerId("non-existent-user-id");

        // Assert
        Assertions.assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void findCourseByOwnerIdAndCourseId_shouldReturnCourse_whenOwnerOwnsTheCourse() {
        // Arrange
        CourseCategory courseCategory = courseCategoryRepository.save(
                CourseCategory.builder()
                        .name("java")
                        .slug("java")
                        .displayOrder(1)
                        .build()
        );
        Course savedCourse = courseRepository.save(
                Course.builder()
                        .title("course1")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );

        // Act
        Optional<Course> result = courseRepository
                .findCourseByOwnerIdAndCourseId(user1Id, savedCourse.getId());

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isEqualTo(savedCourse.getId());
        Assertions.assertThat(result.get().getTitle()).isEqualTo("course1");
        Assertions.assertThat(result.get().getOwnerId()).isEqualTo(user1Id);
    }

    @Test
    void findCourseByOwnerIdAndCourseId_shouldReturnOnlyTheRequestedCourse_whenOwnerHasMultipleCourses() {
        // Arrange
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

        // Act
        Optional<Course> result = courseRepository
                .findCourseByOwnerIdAndCourseId(user1Id, savedCourse2.getId());

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId())
                .isEqualTo(savedCourse2.getId())
                .isNotEqualTo(savedCourse1.getId());
        Assertions.assertThat(result.get().getTitle()).isEqualTo("course2");
    }

    @Test
    void findCourseByOwnerIdAndCourseId_shouldReturnEmpty_whenCourseBelongsToAnotherOwner() {
        // Arrange
        CourseCategory courseCategory = courseCategoryRepository.save(
                CourseCategory.builder()
                        .name("java")
                        .slug("java")
                        .displayOrder(1)
                        .build()
        );
        Course savedCourse = courseRepository.save(
                Course.builder()
                        .title("course1")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );

        // Act
        Optional<Course> result = courseRepository
                .findCourseByOwnerIdAndCourseId(user2Id, savedCourse.getId());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findCourseByOwnerIdAndCourseId_shouldReturnEmpty_whenCourseDoesNotExist() {
        // Arrange
        CourseCategory courseCategory = courseCategoryRepository.save(
                CourseCategory.builder()
                        .name("java")
                        .slug("java")
                        .displayOrder(1)
                        .build()
        );
        courseRepository.save(
                Course.builder()
                        .title("course1")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );

        // Act
        Optional<Course> result = courseRepository
                .findCourseByOwnerIdAndCourseId(user1Id, UUID.randomUUID());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findCourseByOwnerIdAndCourseId_shouldReturnEmpty_whenOwnerDoesNotExist() {
        // Arrange
        CourseCategory courseCategory = courseCategoryRepository.save(
                CourseCategory.builder()
                        .name("java")
                        .slug("java")
                        .displayOrder(1)
                        .build()
        );
        Course savedCourse = courseRepository.save(
                Course.builder()
                        .title("course1")
                        .category(courseCategory)
                        .visibility(CourseVisibility.PUBLIC)
                        .ownerId(user1Id)
                        .build()
        );

        // Act
        Optional<Course> result = courseRepository
                .findCourseByOwnerIdAndCourseId("non-existent-user-id", savedCourse.getId());

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

}
