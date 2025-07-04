package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMonthlyStats {
    // 주간 통계
    private Integer weeklyExerciseCount;
    
    // 월간 통계
    private Integer monthlyExerciseCount;
}
