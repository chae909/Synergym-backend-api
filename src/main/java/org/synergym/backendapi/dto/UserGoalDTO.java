// src/main/java/org/synergym/backendapi/dto/UserGoalDTO.java
package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserGoalDTO {

    @JsonProperty("weekly_goal")
    private String weeklyGoal;

    @JsonProperty("monthly_goal")
    private String monthlyGoal;
}