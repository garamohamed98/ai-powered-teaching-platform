package com.mohamedgara.ai_teaching_platform.courses.repository;

import com.mohamedgara.ai_teaching_platform.TestcontainersConfiguration;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitle;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class LessonRespositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Test
    void findLessonIdAndTitleListByCourseId_shouldReturnLessonsWhenCourseHasLessons() {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourse).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourse).build());

        List<LessonTitle> result = lessonRepository.findLessonIdAndTitleListByCourseId(savedCourse.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(lesson -> lesson.id().equals(savedLessonOne.getId()) && lesson.title().equals("Lesson One")));
        assertTrue(result.stream().anyMatch(lesson -> lesson.id().equals(savedLessonTwo.getId()) && lesson.title().equals("Lesson Two")));
    }

    @Test
    void findLessonIdAndTitleListByCourseId_shouldReturnOnlyLessonsOfGivenCourse() {
        Course savedCourseOne = courseRepository.save(Course.builder().title("Course One").build());
        Course savedCourseTwo = courseRepository.save(Course.builder().title("Course Two").build());
        Lesson savedLessonOne = lessonRepository.save(Lesson.builder().title("Lesson One").course(savedCourseOne).build());
        Lesson savedLessonTwo = lessonRepository.save(Lesson.builder().title("Lesson Two").course(savedCourseTwo).build());

        List<LessonTitle> result = lessonRepository.findLessonIdAndTitleListByCourseId(savedCourseOne.getId());

        assertEquals(1, result.size());
        assertEquals(savedLessonOne.getId(), result.get(0).id());
        assertEquals("Lesson One", result.get(0).title());
        assertTrue(result.stream().noneMatch(lesson -> lesson.id().equals(savedLessonTwo.getId())));
    }

    @Test
    void findLessonIdAndTitleListByCourseId_shouldReturnEmptyListWhenCourseHasNoLessons() {
        Course savedCourse = courseRepository.save(Course.builder().title("Course Title").build());

        List<LessonTitle> result = lessonRepository.findLessonIdAndTitleListByCourseId(savedCourse.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findLessonIdAndTitleListByCourseId_shouldReturnEmptyListWhenCourseDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        List<LessonTitle> result = lessonRepository.findLessonIdAndTitleListByCourseId(randomId);

        assertTrue(result.isEmpty());
    }
}
