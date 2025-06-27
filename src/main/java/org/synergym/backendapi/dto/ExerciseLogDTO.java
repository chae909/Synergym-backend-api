package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ExerciseLogDTO: 운동 기록 정보 전달용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLogDTO {
    // Response용 필드
    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Character useYn;

    // Request/Response 공통 필드
    private Integer userId;
    private Integer routineId;
    private LocalDate exerciseDate;
    private BigDecimal completionRate;
    private String memo;
}
