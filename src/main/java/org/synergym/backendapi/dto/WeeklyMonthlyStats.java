package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

// 주/월간 운동 통계 DTO
public class WeeklyMonthlyStats {
    // 주간 통계
    private Integer weeklyExerciseCount; // 주간 운동 횟수
    
    // 월간 통계
    private Integer monthlyExerciseCount; // 월간 운동 횟수
}
