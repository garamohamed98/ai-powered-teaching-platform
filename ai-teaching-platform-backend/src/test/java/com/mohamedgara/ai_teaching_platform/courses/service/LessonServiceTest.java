package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.LessonInfo;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitle;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitleAndContent;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonResponseMapper lessonResponseMapper;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private LessonService lessonService;

    private UUID lessonId;
    private UUID courseId;
    private Lesson lesson;
    private LessonTitleUpdateRequest request;
    private LessonContentUpdateRequest contentRequest;
    private Course course;

    @BeforeEach
    void setUp() {
        lessonId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        course = Course.builder()
                .id(UUID.randomUUID())
                .build();
        lesson = Lesson.builder()
                .id(lessonId)
                .title("Old Title")
                .content("Some content")
                .course(course)
                .build();
        request = new LessonTitleUpdateRequest("New Title");
        contentRequest = new LessonContentUpdateRequest("New content");
    }

    @Test
    void updateLessonTitle_shouldUpdateTitleAndReturnResponse() {
        LessonResponse expectedResponse = new LessonResponse(lessonId, "New Title", "Some content", course.getId());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);
        when(lessonResponseMapper.toLessonResponse(lesson)).thenReturn(expectedResponse);

        LessonResponse response = lessonService.updateLessonTitle(lessonId, request);

        assertNotNull(response);
        assertEquals("New Title", response.title());
        assertEquals(lessonId, response.id());
        assertEquals("Some content", response.content());
        assertEquals(course.getId(), response.courseId());

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).save(lesson);
        verify(lessonResponseMapper).toLessonResponse(lesson);
    }

    @Test
    void updateLessonTitle_shouldSetNewTitleOnLesson() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson saved = invocation.getArgument(0);
            assertEquals("New Title", saved.getTitle());
            return saved;
        });
        when(lessonResponseMapper.toLessonResponse(any(Lesson.class)))
                .thenReturn(new LessonResponse(lessonId, "New Title", "Some content", course.getId()));

        lessonService.updateLessonTitle(lessonId, request);

        assertEquals("New Title", lesson.getTitle());
    }

    @Test
    void updateLessonTitle_shouldThrowLessonNotFoundException_whenLessonDoesNotExist() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class,
                () -> lessonService.updateLessonTitle(lessonId, request));

        assertEquals("Lesson not Found", exception.getMessage());

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository, never()).save(any());
        verify(lessonResponseMapper, never()).toLessonResponse(any());
    }

    @Test
    void updateLessonTitle_shouldReturnLessonNotFoundForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class,
                () -> lessonService.updateLessonTitle(invalidId, request));
    }

    @Test
    void updateLessonContent_shouldUpdateContentAndReturnResponse() {
        LessonResponse expectedResponse = new LessonResponse(lessonId, "Old Title", "New content", course.getId());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);
        when(lessonResponseMapper.toLessonResponse(lesson)).thenReturn(expectedResponse);

        LessonResponse response = lessonService.updateLessonContent(lessonId, contentRequest);

        assertNotNull(response);
        assertEquals("New content", response.content());
        assertEquals(lessonId, response.id());
        assertEquals("Old Title", response.title());
        assertEquals(course.getId(), response.courseId());

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).save(lesson);
        verify(lessonResponseMapper).toLessonResponse(lesson);
    }

    @Test
    void updateLessonContent_shouldSetNewContentOnLesson() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson saved = invocation.getArgument(0);
            assertEquals("New content", saved.getContent());
            return saved;
        });
        when(lessonResponseMapper.toLessonResponse(any(Lesson.class)))
                .thenReturn(new LessonResponse(lessonId, "Old Title", "New content", course.getId()));

        lessonService.updateLessonContent(lessonId, contentRequest);

        assertEquals("New content", lesson.getContent());
    }

    @Test
    void updateLessonContent_shouldThrowLessonNotFoundException_whenLessonDoesNotExist() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class,
                () -> lessonService.updateLessonContent(lessonId, contentRequest));

        assertEquals("Lesson not Found", exception.getMessage());

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository, never()).save(any());
        verify(lessonResponseMapper, never()).toLessonResponse(any());
    }

    @Test
    void updateLessonContent_shouldReturnLessonNotFoundForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class,
                () -> lessonService.updateLessonContent(invalidId, contentRequest));
    }

    @Test
    void getLessonById_shouldReturnLessonResponse_whenLessonExists() {
        LessonResponse expectedResponse = new LessonResponse(lessonId, "Old Title", "Some content", course.getId());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonResponseMapper.toLessonResponse(lesson)).thenReturn(expectedResponse);

        LessonResponse response = lessonService.getLessonById(lessonId);

        assertNotNull(response);
        assertEquals(lessonId, response.id());
        assertEquals("Old Title", response.title());
        assertEquals("Some content", response.content());
        assertEquals(course.getId(), response.courseId());

        verify(lessonRepository).findById(lessonId);
        verify(lessonResponseMapper).toLessonResponse(lesson);
    }

    @Test
    void getLessonById_shouldThrowLessonNotFoundException_whenLessonDoesNotExist() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class,
                () -> lessonService.getLessonById(lessonId));

        assertEquals("Lesson not Found", exception.getMessage());

        verify(lessonRepository).findById(lessonId);
        verify(lessonResponseMapper, never()).toLessonResponse(any());
    }

    @Test
    void getLessonById_shouldReturnLessonNotFoundForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class,
                () -> lessonService.getLessonById(invalidId));
    }

    @Test
    void deleteLesson_shouldDeleteLesson_whenLessonExists() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        lessonService.deleteLesson(lessonId);

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository).delete(lesson);
    }

    @Test
    void deleteLesson_shouldThrowLessonNotFoundException_whenLessonDoesNotExist() {
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        LessonNotFoundException exception = assertThrows(LessonNotFoundException.class,
                () -> lessonService.deleteLesson(lessonId));

        assertEquals("Lesson not Found", exception.getMessage());

        verify(lessonRepository).findById(lessonId);
        verify(lessonRepository, never()).delete(any());
    }

    @Test
    void deleteLesson_shouldReturnLessonNotFoundForAnyInvalidId() {
        UUID invalidId = UUID.randomUUID();
        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(LessonNotFoundException.class,
                () -> lessonService.deleteLesson(invalidId));
    }

    @Test
    void lessonListExists_shouldReturnTrue_whenAllLessonsExist() {
        List<UUID> lessonIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(lessonRepository.countExistingLessons(lessonIds)).thenReturn((long) lessonIds.size());

        boolean exists = lessonService.lessonListExists(lessonIds);

        assertTrue(exists);

        verify(lessonRepository).countExistingLessons(lessonIds);
    }

    @Test
    void lessonListExists_shouldReturnFalse_whenSomeOrAllLessonsDoNotExist() {
        List<UUID> lessonIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(lessonRepository.countExistingLessons(lessonIds)).thenReturn(1L);

        boolean exists = lessonService.lessonListExists(lessonIds);

        assertFalse(exists);
        verify(lessonRepository).countExistingLessons(lessonIds);
    }

    @Test
    void lessonListExists_shouldReturnTrue_whenListIsEmpty() {
        List<UUID> emptyList = Collections.emptyList();
        when(lessonRepository.countExistingLessons(emptyList)).thenReturn(0L);

        boolean exists = lessonService.lessonListExists(emptyList);

        assertTrue(exists);
        verify(lessonRepository).countExistingLessons(emptyList);
    }

    @Test
    void getLessonSummaryByCourseId_shouldReturnMapOfLessonIdsAndTitles_whenCourseExistsAndLessonsExist() {
        LessonTitle lessonTitleOne = new LessonTitle(UUID.randomUUID(), "Lesson One");
        LessonTitle lessonTitleTwo = new LessonTitle(UUID.randomUUID(), "Lesson Two");
        List<LessonTitle> lessonTitles = List.of(lessonTitleOne, lessonTitleTwo);

        when(courseService.courseExists(courseId)).thenReturn(true);
        when(lessonRepository.findLessonIdAndTitleListByCourseId(courseId)).thenReturn(lessonTitles);

        Map<UUID, String> result = lessonService.getLessonSummaryByCourseId(courseId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Lesson One", result.get(lessonTitleOne.id()));
        assertEquals("Lesson Two", result.get(lessonTitleTwo.id()));

        verify(courseService).courseExists(courseId);
        verify(lessonRepository).findLessonIdAndTitleListByCourseId(courseId);
    }

    @Test
    void getLessonSummaryByCourseId_shouldReturnEmptyMap_whenCourseExistsButNoLessonsExist() {
        when(courseService.courseExists(courseId)).thenReturn(true);
        when(lessonRepository.findLessonIdAndTitleListByCourseId(courseId)).thenReturn(List.of());

        Map<UUID, String> result = lessonService.getLessonSummaryByCourseId(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseService).courseExists(courseId);
        verify(lessonRepository).findLessonIdAndTitleListByCourseId(courseId);
    }

    @Test
    void getLessonSummaryByCourseId_shouldReturnEmptyMap_whenCourseDoesNotExist() {
        when(courseService.courseExists(courseId)).thenReturn(false);

        Map<UUID, String> result = lessonService.getLessonSummaryByCourseId(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseService).courseExists(courseId);
        verify(lessonRepository, never()).findLessonIdAndTitleListByCourseId(any());
    }

    @Test
    void getCourseLessonInfoList_shouldReturnLessonInfoList_whenCourseExistsAndLessonsExist() {
        LessonTitleAndContent lessonOne = new LessonTitleAndContent(UUID.randomUUID(), "Lesson One", "Content One");
        LessonTitleAndContent lessonTwo = new LessonTitleAndContent(UUID.randomUUID(), "Lesson Two", "Content Two");
        List<LessonTitleAndContent> lessonInfos = List.of(lessonOne, lessonTwo);

        when(courseService.courseExists(courseId)).thenReturn(true);
        when(lessonRepository.findLessonTitleAndContentByCourseId(courseId)).thenReturn(lessonInfos);

        List<LessonInfo> result = lessonService.getCourseLessonInfoList(courseId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(lessonOne.id(), result.get(0).id());
        assertEquals("Lesson One", result.get(0).title());
        assertEquals("Content One", result.get(0).content());
        assertEquals(lessonTwo.id(), result.get(1).id());
        assertEquals("Lesson Two", result.get(1).title());
        assertEquals("Content Two", result.get(1).content());

        verify(courseService).courseExists(courseId);
        verify(lessonRepository).findLessonTitleAndContentByCourseId(courseId);
    }

    @Test
    void getCourseLessonInfoList_shouldReturnEmptyList_whenCourseExistsButNoLessonsExist() {
        when(courseService.courseExists(courseId)).thenReturn(true);
        when(lessonRepository.findLessonTitleAndContentByCourseId(courseId)).thenReturn(List.of());

        List<LessonInfo> result = lessonService.getCourseLessonInfoList(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseService).courseExists(courseId);
        verify(lessonRepository).findLessonTitleAndContentByCourseId(courseId);
    }

    @Test
    void getCourseLessonInfoList_shouldReturnEmptyList_whenCourseDoesNotExist() {
        when(courseService.courseExists(courseId)).thenReturn(false);

        List<LessonInfo> result = lessonService.getCourseLessonInfoList(courseId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courseService).courseExists(courseId);
        verify(lessonRepository, never()).findLessonTitleAndContentByCourseId(any());
    }

    @Test
    void getLessonInfoList_shouldReturnLessonInfoList_whenLessonIdsExist() {
        UUID lessonIdOne = UUID.randomUUID();
        UUID lessonIdTwo = UUID.randomUUID();
        LessonTitleAndContent lessonOne = new LessonTitleAndContent(lessonIdOne, "Lesson One", "Content One");
        LessonTitleAndContent lessonTwo = new LessonTitleAndContent(lessonIdTwo, "Lesson Two", "Content Two");
        List<UUID> lessonIds = List.of(lessonIdOne, lessonIdTwo);

        when(lessonRepository.findLessonTitleAndContentByLessonIdList(lessonIds)).thenReturn(List.of(lessonOne, lessonTwo));

        List<LessonInfo> result = lessonService.getLessonInfoList(lessonIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(lessonIdOne, result.get(0).id());
        assertEquals("Lesson One", result.get(0).title());
        assertEquals("Content One", result.get(0).content());
        assertEquals(lessonIdTwo, result.get(1).id());
        assertEquals("Lesson Two", result.get(1).title());
        assertEquals("Content Two", result.get(1).content());

        verify(lessonRepository).findLessonTitleAndContentByLessonIdList(lessonIds);
    }

    @Test
    void getLessonInfoList_shouldReturnOnlyExistingLessons_whenSomeLessonIdsDoNotExist() {
        UUID lessonIdOne = UUID.randomUUID();
        LessonTitleAndContent lessonOne = new LessonTitleAndContent(lessonIdOne, "Lesson One", "Content One");
        List<UUID> lessonIds = List.of(lessonIdOne, UUID.randomUUID());

        when(lessonRepository.findLessonTitleAndContentByLessonIdList(lessonIds)).thenReturn(List.of(lessonOne));

        List<LessonInfo> result = lessonService.getLessonInfoList(lessonIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(lessonIdOne, result.get(0).id());
        assertEquals("Lesson One", result.get(0).title());
        assertEquals("Content One", result.get(0).content());

        verify(lessonRepository).findLessonTitleAndContentByLessonIdList(lessonIds);
    }

    @Test
    void getLessonInfoList_shouldReturnEmptyList_whenNoLessonIdsExist() {
        List<UUID> lessonIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(lessonRepository.findLessonTitleAndContentByLessonIdList(lessonIds)).thenReturn(List.of());

        List<LessonInfo> result = lessonService.getLessonInfoList(lessonIds);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(lessonRepository).findLessonTitleAndContentByLessonIdList(lessonIds);
    }
}
