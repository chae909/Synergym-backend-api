package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.entity.Exercise;
import java.util.List;

public interface ExerciseLikeService {
    // 운동 좋아요 저장
    void saveExerciseLike(ExerciseLikeDTO exerciseLikeDTO);

    // 운동 좋아요 삭제
    void deleteExerciseLike(Integer userId, Integer exerciseId);

    // 특정 사용자의 모든 운동 좋아요 조회
    List<ExerciseLikeDTO> findLikesByUser(Integer userId);

    // 특정 운동의 모든 좋아요 조회
    List<ExerciseLikeDTO> findLikesByExercise(Integer exerciseId);

    // 특정 사용자가 특정 운동을 좋아요 했는지 확인
    boolean existsByUserAndExercise(Integer userId, Integer exerciseId);

    // Entity -> DTO 변환
    default ExerciseLikeDTO entityToDto(ExerciseLike like) {
        return ExerciseLikeDTO.builder()
                .userId(like.getUser().getId())
                .exerciseId(like.getExercise().getId())
                .build();
    }

    // DTO -> Entity 변환
    default ExerciseLike dtoToEntity(ExerciseLikeDTO dto, User user, Exercise exercise) {
        return ExerciseLike.builder()
                .user(user)
                .exercise(exercise)
                .build();
    }
} 