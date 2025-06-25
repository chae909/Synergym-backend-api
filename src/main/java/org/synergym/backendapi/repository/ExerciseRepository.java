package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
}
