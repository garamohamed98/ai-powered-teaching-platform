package com.mohamedgara.ai_teaching_platform.courses.repository;

import com.mohamedgara.ai_teaching_platform.courses.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
}
