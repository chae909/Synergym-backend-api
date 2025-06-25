package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Routine;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
}