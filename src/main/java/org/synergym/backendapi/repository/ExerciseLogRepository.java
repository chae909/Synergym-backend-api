package org.synergym.backendapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.User;
import java.util.Map;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Integer> {
    
    // 날짜로 운동 기록 조회
    List<ExerciseLog> findByExerciseDate(LocalDate date);
    
    // 통계를 위한 쿼리 메서드들
    @Query("SELECT COUNT(e) FROM ExerciseLog e WHERE e.user.id = :userId AND e.exerciseDate BETWEEN :startDate AND :endDate")
    Integer countByUserIdAndDateBetween(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // 사용자별 운동 기록 조회 (날짜 범위)
    @Query("SELECT e FROM ExerciseLog e WHERE e.user.id = :userId AND e.exerciseDate BETWEEN :startDate AND :endDate ORDER BY e.exerciseDate DESC")
    List<ExerciseLog> findByUserIdAndDateBetween(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e.user.id as userId, COUNT(e) as count FROM ExerciseLog e WHERE e.exerciseDate BETWEEN :startDate AND :endDate GROUP BY e.user.id")
    List<Map<String, Object>> countLogsGroupByUser(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 사용자와 날짜로 운동 기록 조회
    Optional<ExerciseLog> findByUserAndExerciseDate(User user, LocalDate date);
}
