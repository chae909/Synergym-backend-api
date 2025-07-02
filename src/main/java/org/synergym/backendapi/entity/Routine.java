package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Routines")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("use_yn = 'Y'")
public class Routine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExercise> exercises = new ArrayList<>();

    @Builder
    public Routine(User user, String name, String description) {
        this.user = user;
        this.name = name;
        this.description = description;
    }

    public void updateExercises(List<RoutineExercise> newExercises) {
        this.exercises.clear();
        if (newExercises != null) {
            this.exercises.addAll(newExercises);
            newExercises.forEach(exercise -> exercise.updateRoutine(this));
        }
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }
}