package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GoalRequestDTO {
    @JsonProperty("exercise_history")
    private List<ExerciseLogDTO> exerciseHistory;

    @JsonProperty("coach_persona")
    private String coachPersona;
}