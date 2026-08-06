package com.mohamedgara.ai_teaching_platform.courses.repository;

import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import com.mohamedgara.ai_teaching_platform.courses.projection.LessonTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByCourseId(UUID courseId);

    @Query("""
            SELECT l.id, l.title
            FROM Lesson l
            WHERE l.course.id =:courseId
            """)
    List<LessonTitle> findLessonIdAndTitleListByCourseId(@Param("courseId") UUID courseId);
}
