// src/main/java/org/synergym/backendapi/dto/ComparisonStatsDTO.java
package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonStatsDTO {
    private double frequencyPercentile;
    private String comment;
}