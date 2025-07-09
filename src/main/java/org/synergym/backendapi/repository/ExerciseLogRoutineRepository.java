package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.ExerciseLogRoutine;
import org.synergym.backendapi.entity.ExerciseLogRoutineId;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.Routine;
import java.util.List;

public interface ExerciseLogRoutineRepository extends JpaRepository<ExerciseLogRoutine, ExerciseLogRoutineId> {

    // 운동 기록에 해당하는 루틴 조회
    List<ExerciseLogRoutine> findByExerciseLog(ExerciseLog exerciseLog);

    // 루틴에 해당하는 운동 기록 조회
    List<ExerciseLogRoutine> findByRoutine(Routine routine);

    // 여러 운동 기록에 해당하는 루틴 조회
    List<ExerciseLogRoutine> findByExerciseLogIn(List<ExerciseLog> exerciseLogs);
} 