package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 운동 기록 루틴 관련 DTO
public class ExerciseLogRoutineDTO {
    private int exerciseLogId; // 운동 기록 ID
    private int routineId; // 루틴 ID
    private String routineName; // 루틴 이름
    private Character checkYn; // 체크 여부
}