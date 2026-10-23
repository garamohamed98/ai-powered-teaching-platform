package com.mohamedgara.ai_teaching_platform.courses.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;

    @Column(nullable = false)
    private String ownerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private CourseCategory category;

    @Column(nullable = false)
    private CourseVisibility visibility;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist(){this.createdAt = LocalDateTime.now();}

}
