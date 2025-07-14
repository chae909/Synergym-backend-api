package org.synergym.backendapi.service;

import java.util.List;

import org.synergym.backendapi.dto.EmotionLogDTO;
import org.synergym.backendapi.dto.EmotionStatsDTO;
import org.synergym.backendapi.entity.EmotionLog;

public interface EmotionLogService {

    // 감성 기록 저장 또는 업데이트
    EmotionLogDTO saveOrUpdateEmotionLog(EmotionLogDTO emotionLogDTO);

    // 사용자별 감성 기록 조회
    List<EmotionLogDTO> getEmotionLogsByUser(Integer userId);
    
    // 감성 기록 삭제
    void deleteEmotionLog(Integer emotionLogId);

    // 감정 통계 조회
    EmotionStatsDTO getEmotionStats(Integer userId);

    // Entity -> DTO 변환 (ExerciseLog의 memo를 포함)
    default EmotionLogDTO entityToDTO(EmotionLog log) {
        return EmotionLogDTO.builder()
                .id(log.getId())
                .userId(log.getExerciseLog().getUser().getId())
                .exerciseDate(log.getExerciseLog().getExerciseDate())
                .emotion(log.getEmotion())
                .memo(log.getExerciseLog().getMemo())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .build();
    }
}