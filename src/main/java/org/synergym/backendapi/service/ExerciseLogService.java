package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.entity.ExerciseLog;
import java.util.List;

public interface ExerciseLogService {
    // 운동 기록 저장
    Integer saveExerciseLog(ExerciseLogDTO exerciseLogDTO);

    // 모든 운동 기록 조회
    List<ExerciseLogDTO> findAllExerciseLogs();

    // ID로 운동 기록 조회
    ExerciseLogDTO findExerciseLogById(Integer id);

    // ID로 운동 기록 삭제
    void deleteExerciseLog(Integer id);

    // ID로 운동 기록 수정
    void updateExerciseLog(Integer id, ExerciseLogDTO exerciseLogDTO);

    // DTO -> Entity 변환
    default ExerciseLog dtoToEntity(ExerciseLogDTO dto) {
        return ExerciseLog.builder()
                // user, routine은 ServiceImpl에서 주입
                .exerciseDate(dto.getExerciseDate())
                .completionRate(dto.getCompletionRate())
                .memo(dto.getMemo())
                .build();
    }

    // Entity -> DTO 변환
    default ExerciseLogDTO entityToDto(ExerciseLog log) {
        return ExerciseLogDTO.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .routineId(log.getRoutine() != null ? log.getRoutine().getId() : null)
                .exerciseDate(log.getExerciseDate())
                .completionRate(log.getCompletionRate())
                .memo(log.getMemo())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .useYn(log.getUseYn())
                .build();
    }

    // Entity 필드 업데이트 (부분 수정용)
    default void updateEntityFromDto(ExerciseLog log, ExerciseLogDTO dto) {
        if (dto.getExerciseDate() != null) log.updateExerciseDate(dto.getExerciseDate());
        if (dto.getCompletionRate() != null) log.updateCompletionRate(dto.getCompletionRate());
        if (dto.getMemo() != null) log.updateMemo(dto.getMemo());
        // user, routine은 ServiceImpl에서 처리
    }
} 