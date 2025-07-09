package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ExerciseLogDTO: 운동 기록 정보 전달용 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// 운동 기록 관련 DTO
public class ExerciseLogDTO {
    // Response용 필드
    private Integer id; // 운동 기록 ID
    private LocalDateTime createdAt; // 생성 일시
    private LocalDateTime updatedAt; // 수정 일시
    private Character useYn; // 사용 여부

    // Request/Response 공통 필드
    private Integer userId; // 사용자 ID
    private Integer routineId; // 루틴 ID
    private LocalDate exerciseDate; // 운동 날짜
    private BigDecimal completionRate; // 완료율
    private String memo; // 메모
    private List<Integer> routineIds; // 루틴 ID 리스트
    private List<String> routineNames; // 루틴 이름 리스트
}
