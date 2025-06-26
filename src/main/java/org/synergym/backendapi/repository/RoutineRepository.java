package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Routine;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {
    List<Routine> findByName(String name);
}