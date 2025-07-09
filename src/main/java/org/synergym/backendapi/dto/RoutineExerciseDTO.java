package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 루틴에 추가된 운동 관련 DTO
public class RoutineExerciseDTO {

    private Integer id; // 루틴에 추가된 운동의 고유 id
    private Integer routineId; // 루틴 id
    private Integer exerciseId; // 운동 id
    private String exerciseName; // 운동 이름
    private Integer order; // 루틴에 추가된 운동의 순서
    private Character checkYn; // 사용자의 운동 기록에서 체크 됐는지에 대한 여부

}
