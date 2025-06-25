package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.ExerciseLog;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Integer> {

}
