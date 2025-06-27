package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercise_log_routine")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseLogRoutine {

    @EmbeddedId
    private ExerciseLogRoutineId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseLogId")
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("routineId")
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Builder
    public ExerciseLogRoutine(ExerciseLog exerciseLog, Routine routine) {
        this.id = new ExerciseLogRoutineId(exerciseLog.getId(), routine.getId());
        this.exerciseLog = exerciseLog;
        this.routine = routine;
    }
} 