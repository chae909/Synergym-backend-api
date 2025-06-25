package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.RoutineExerciseId;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, RoutineExerciseId> {
}
