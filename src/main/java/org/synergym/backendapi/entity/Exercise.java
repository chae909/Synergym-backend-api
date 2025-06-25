package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Exercises")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "effects", columnDefinition = "TEXT")
    private String effects;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "posture", length = 50)
    private String posture;

    @Column(name = "body_part", length = 50)
    private String bodyPart;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @Builder
    public Exercise(String name, String category, String description, String effects, String difficulty, String posture, String bodyPart, String thumbnailUrl) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.effects = effects;
        this.difficulty = difficulty;
        this.posture = posture;
        this.bodyPart = bodyPart;
        this.thumbnailUrl = thumbnailUrl;
    }
}