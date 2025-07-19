package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoalDetailDTO {
    @JsonProperty("workouts")
    private int workouts;

    @JsonProperty("completion_rate")
    private int completionRate;
}