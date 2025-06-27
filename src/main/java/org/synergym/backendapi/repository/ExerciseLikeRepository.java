package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.ExerciseLikeId;

import java.util.List;
import java.util.Optional;

public interface ExerciseLikeRepository extends JpaRepository<ExerciseLike, ExerciseLikeId> {
    
    // 특정 사용자의 모든 운동 좋아요 조회
    @Query("SELECT el FROM ExerciseLike el WHERE el.user.id = :userId")
    List<ExerciseLike> findByUserId(@Param("userId") Integer userId);
    
    // 특정 운동의 모든 좋아요 조회
    @Query("SELECT el FROM ExerciseLike el WHERE el.exercise.id = :exerciseId")
    List<ExerciseLike> findByExerciseId(@Param("exerciseId") Integer exerciseId);
    
    // 특정 사용자와 운동의 좋아요 조회 (존재 여부 확인용)
    @Query("SELECT el FROM ExerciseLike el WHERE el.user.id = :userId AND el.exercise.id = :exerciseId")
    Optional<ExerciseLike> findByUserIdAndExerciseId(@Param("userId") Integer userId, @Param("exerciseId") Integer exerciseId);
    
    // 특정 사용자와 운동의 좋아요 존재 여부 확인
    boolean existsByUserIdAndExerciseId(Integer userId, Integer exerciseId);
}