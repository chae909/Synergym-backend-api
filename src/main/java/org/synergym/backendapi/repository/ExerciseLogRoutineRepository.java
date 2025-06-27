package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.ExerciseLogRoutine;
import org.synergym.backendapi.entity.ExerciseLogRoutineId;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.Routine;
import java.util.List;

public interface ExerciseLogRoutineRepository extends JpaRepository<ExerciseLogRoutine, ExerciseLogRoutineId> {
    List<ExerciseLogRoutine> findByExerciseLog(ExerciseLog exerciseLog);
    List<ExerciseLogRoutine> findByRoutine(Routine routine);
    List<ExerciseLogRoutine> findByExerciseLogIn(List<ExerciseLog> exerciseLogs);
} 