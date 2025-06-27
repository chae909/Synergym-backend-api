package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.ExerciseLog;

import java.time.LocalDate;
import java.util.List;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Integer> {
    
    // 날짜로 운동 기록 조회
    List<ExerciseLog> findByExerciseDate(LocalDate date);
}
