package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.entity.Exercise;
import java.util.List;

public interface ExerciseLikeService {
    // 운동 좋아요 추가
    void add(ExerciseLikeDTO exerciseLikeDTO);

    // 운동 좋아요 삭제
    void delete(Integer userId, Integer exerciseId);

    // 특정 사용자의 모든 운동 좋아요 조회
    List<ExerciseLikeDTO> getByUser(Integer userId);

    // 특정 운동의 모든 좋아요 조회
    List<ExerciseLikeDTO> getByExercise(Integer exerciseId);

    // 특정 사용자가 특정 운동을 좋아요 했는지 확인(중복 좋아요 방지)
    boolean isLiked(Integer userId, Integer exerciseId);

    // DTO -> Entity 변환
    default ExerciseLike DTOtoEntity(ExerciseLikeDTO dto, User user, Exercise exercise) {
        return ExerciseLike.builder()
                .user(user)
                .exercise(exercise)
                .build();
    }

    // Entity -> DTO 변환
    default ExerciseLikeDTO entityToDTO(ExerciseLike like) {
        return ExerciseLikeDTO.builder()
                .userId(like.getUser().getId())
                .exerciseId(like.getExercise().getId())
                .build();
    }
} 