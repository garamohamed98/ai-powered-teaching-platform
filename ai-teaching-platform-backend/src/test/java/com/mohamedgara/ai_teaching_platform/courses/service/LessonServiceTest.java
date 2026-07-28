package com.mohamedgara.ai_teaching_platform.courses.service;

import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonContentUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.request.LessonTitleUpdateRequest;
import com.mohamedgara.ai_teaching_platform.courses.dto.response.LessonResponse;
import com.mohamedgara.ai_teaching_platform.courses.entity.Course;
import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.exception.LessonNotFoundException;
import com.mohamedgara.ai_teaching_platform.courses.mappers.LessonResponseMapper;
import com.mohamedgara.ai_teaching_platform.courses.repository.LessonRepository;
import com.mohamedgara.ai_teaching_platform.courses.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonResponseMapper lessonResponseMapper;

    @InjectMocks
    private LessonService lessonService;

    private UUID lessonId;
    private Lesson lesson;
    private LessonTitleUpdateRequest request;
    private LessonContentUpdateRequest contentRequest;
    private Course course;

    @BeforeEach
    void setUp() {
        lessonId = UUID.randomUUID();
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
}
