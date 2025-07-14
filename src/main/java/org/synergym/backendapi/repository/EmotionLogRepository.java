package org.synergym.backendapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.EmotionLog;
import org.synergym.backendapi.entity.ExerciseLog;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Integer> {
    
    // ExerciseLog 엔티티로 EmotionLog 조회
    Optional<EmotionLog> findByExerciseLog(ExerciseLog exerciseLog);

    // 사용자로 모든 감성 기록 조회 (Join Fetch 사용)
    @Query("SELECT el FROM EmotionLog el JOIN FETCH el.exerciseLog exl WHERE exl.user.id = :userId")
    List<EmotionLog> findByUserWithExerciseLog(@Param("userId") Integer userId);

    // 특정 기간 동안의 감정별 카운트 조회
    @Query("SELECT el.emotion, COUNT(el) FROM EmotionLog el " +
           "WHERE el.exerciseLog.user.id = :userId AND el.exerciseLog.exerciseDate BETWEEN :startDate AND :endDate " +
           "GROUP BY el.emotion")
    List<Object[]> countEmotionsByDateRange(@Param("userId") Integer userId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
}