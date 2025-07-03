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

    @Column(name = "check_yn", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private Character checkYn;

    @Builder
    public ExerciseLogRoutine(ExerciseLog exerciseLog, Routine routine) {
        this.id = new ExerciseLogRoutineId(exerciseLog.getId(), routine.getId());
        this.exerciseLog = exerciseLog;
        this.routine = routine;
        this.checkYn = 'N'; // '미완료' 상태로 기본값 설정
    }

    /**
     * 운동 완료 여부(checkYn)를 수정하는 메서드
     * @param checkYn 'Y' 또는 'N'
     */
    public void updateCheckYn(Character checkYn) {
        this.checkYn = checkYn;
    }
}