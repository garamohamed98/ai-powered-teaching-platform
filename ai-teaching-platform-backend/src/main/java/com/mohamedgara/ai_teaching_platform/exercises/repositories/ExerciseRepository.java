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
            SELECT e from Exercise e
            WHERE e.lessonId IN :lessonIdList
            """)
    List<Exercise> findExercises(@Param("lessonIdList") List<UUID> lessonIdList);
}
