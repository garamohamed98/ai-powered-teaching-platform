package com.mohamedgara.ai_teaching_platform.exercises.repositories;

import com.mohamedgara.ai_teaching_platform.exercises.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    @Query("""
    SELECT e
    FROM Exercise e
    WHERE EXISTS (
        SELECT 1
        FROM e.lessonIdList lessonId
        WHERE lessonId IN :lessonIdList
    )
    """)
    List<Exercise> findExercises(@Param("lessonIdList") List<UUID> lessonIdList);
}
