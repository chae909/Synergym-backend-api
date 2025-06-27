package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ExerciseLikeDTO: 운동 좋아요 정보 전달용 DTO
 * - userId, exerciseId: 입력용 필드
 * - user, exercise: 출력용 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLikeDTO {
    // 입력용 필드
    private Integer userId;
    private Integer exerciseId;

    // 출력용 객체
    private UserDTO user;
    private ExerciseDTO exercise;
}
