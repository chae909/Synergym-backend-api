package org.synergym.backendapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.dto.WeeklyMonthlyStats;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.ExerciseLogRoutine;

public interface ExerciseLogService {
    // 운동기록 생성
    Integer createExerciseLog(ExerciseLogDTO exerciseLogDTO);

    // 모든 운동기록 조회
    List<ExerciseLogDTO> getAllExerciseLogs();

    // ID로 운동기록 조회
    ExerciseLogDTO getExerciseLogById(Integer id);

    // 사용자별 운동기록 조회
    List<ExerciseLogDTO> getExerciseLogsByUser(Integer userId);

    // 사용자별 + 날짜별 운동기록 조회
    List<ExerciseLogDTO> getExerciseLogsByUserAndDate(Integer userId, LocalDate date);

    void updateExerciseLog(Integer id, ExerciseLogDTO exerciseLogDTO);

    // 운동기록 삭제
    void deleteExerciseLog(Integer id);

    // 운동 통계 조회
    WeeklyMonthlyStats getStats(Integer userId, LocalDate weekStart, LocalDate weekEnd, 
                               LocalDate monthStart, LocalDate monthEnd);
                               
    // 주간 운동 통계 조회
    WeeklyMonthlyStats getWeeklyStats(Integer userId);
    
    // 월간 운동 통계 조회
    WeeklyMonthlyStats getMonthlyStats(Integer userId);

    // DTO -> Entity 변환 (ServiceImpl에서 반드시 활용)
    default ExerciseLog DTOtoEntity(ExerciseLogDTO dto) {
        return ExerciseLog.builder()
                .exerciseDate(dto.getExerciseDate())
                .completionRate(dto.getCompletionRate())
                .memo(dto.getMemo())
                .build();
    }

    // Entity -> DTO 변환 (routines 정보 포함, ServiceImpl에서 반드시 활용)
    default ExerciseLogDTO entityToDTO(ExerciseLog log, List<ExerciseLogRoutine> logRoutines) {
        return ExerciseLogDTO.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .exerciseDate(log.getExerciseDate())
                .completionRate(log.getCompletionRate())
                .memo(log.getMemo())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .useYn(log.getUseYn())
                .routineIds(logRoutines.stream().map(lr -> lr.getRoutine().getId()).collect(Collectors.toList()))
                .routineNames(logRoutines.stream().map(lr -> lr.getRoutine().getName()).collect(Collectors.toList()))
                .build();
    }
}