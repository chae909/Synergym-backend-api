package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.RoutineExerciseId;

import java.util.List;
import java.util.Optional;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, RoutineExerciseId> {
    List<RoutineExercise> findByRoutine(Routine routine);
    Optional<RoutineExercise> findByRoutineAndOrder(Routine routine, int order);
}
