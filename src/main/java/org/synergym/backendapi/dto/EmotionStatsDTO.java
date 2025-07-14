package org.synergym.backendapi.dto;

import java.util.Map;

import org.synergym.backendapi.entity.EmotionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionStatsDTO {
    private Map<EmotionType, Long> weeklyStats;
    private Map<EmotionType, Long> monthlyStats;
}