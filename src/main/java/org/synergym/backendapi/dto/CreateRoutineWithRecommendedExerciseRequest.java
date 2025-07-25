package org.synergym.backendapi.dto;

import lombok.Data;

@Data
public class CreateRoutineWithRecommendedExerciseRequest {
    private RoutineDTO routineDTO;
    private int exerciseId; // 운동 id로 변경
    private int order;
} 