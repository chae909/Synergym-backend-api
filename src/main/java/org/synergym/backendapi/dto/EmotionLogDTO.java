package org.synergym.backendapi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.synergym.backendapi.entity.EmotionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLogDTO {
    private int id;
    private Integer userId;
    private LocalDate exerciseDate;
    private EmotionType emotion;
    private Integer logId;
    private String memo; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}