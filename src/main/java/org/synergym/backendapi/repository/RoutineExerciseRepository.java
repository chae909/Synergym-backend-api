package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.RoutineExerciseId;

import java.util.List;
import java.util.Optional;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, RoutineExerciseId> {
    // 특정 루틴에 포함된 모든 운동 조회
    List<RoutineExercise> findByRoutine(Routine routine);
    // 특정 루틴에 포함된 특정 순서의 운동 조회
    Optional<RoutineExercise> findByRoutineAndOrder(Routine routine, int order);
}
