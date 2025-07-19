package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FinalGoalsDTO {
    @JsonProperty("weekly_goal")
    private GoalDetailDTO weeklyGoal;

    @JsonProperty("monthly_goal")
    private GoalDetailDTO monthlyGoal;
}