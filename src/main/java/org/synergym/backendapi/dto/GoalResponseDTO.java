package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDTO {
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("analysis_result")
    private String analysisResult;

    @JsonProperty("final_goals")
    private FinalGoalsDTO finalGoals;

    @JsonProperty("generated_badge")
    private BadgeDTO generatedBadge;
}