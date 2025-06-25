package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Routine_Exercises")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineExercise {

    @EmbeddedId
    private RoutineExerciseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("routineId")
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "`order`", nullable = false)
    private int order;

    @Builder
    public RoutineExercise(Routine routine, Exercise exercise, int order) {
        this.id = new RoutineExerciseId(routine.getId(), exercise.getId());
        this.routine = routine;
        this.exercise = exercise;
        this.order = order;
    }

    public void updateExercise(Exercise newExercise) {
        this.exercise = newExercise;
    }

    public void updateOrder(int newOrder) {
        this.order = newOrder;
    }
}
