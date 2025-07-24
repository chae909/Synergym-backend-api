package org.synergym.backendapi.dto;

import lombok.Data;

@Data
public class EmotionResponseDTO {
    private String label;
    private double score;
}
