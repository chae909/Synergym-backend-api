package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ExerciseLikeDTO: 운동 좋아요 정보 전달용 DTO
 * - userId, exerciseId: 복합키
 * - user, exercise: (선택) 응답용 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLikeDTO {
    // 복합키
    private Integer userId;
    private Integer exerciseId;

    // (선택) 응답용 객체
    private UserDTO user;
    private ExerciseDTO exercise;
}
