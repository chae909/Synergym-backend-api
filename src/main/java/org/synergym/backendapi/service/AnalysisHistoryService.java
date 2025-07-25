package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.AnalysisHistoryDTO;
import org.synergym.backendapi.entity.AnalysisHistory;
import org.synergym.backendapi.entity.User;

import java.util.List;
import java.util.Map;

public interface AnalysisHistoryService {

    /**
     * 분석 이력을 생성합니다.
     * @param requestDTO 생성할 분석 이력 정보 (DTO)
     * @param userId 분석 대상 사용자 ID
     * @return 생성된 분석 이력 정보를 담은 DTO
     */
    AnalysisHistoryDTO createAnalysisHistory(AnalysisHistoryDTO requestDTO, int userId);

    /**
     * 특정 ID의 분석 이력을 조회합니다.
     * @param id 분석 이력 ID
     * @return 조회된 분석 이력 DTO
     */
    AnalysisHistoryDTO getAnalysisHistoryById(int id);

    /**
     * 특정 사용자의 전체 분석 이력 목록을 조회합니다.
     * 최근 생성 순서로 정렬됩니다.
     * @param userId 사용자 ID
     * @return 해당 사용자의 분석 이력 목록 (최신순 정렬)
     */
    List<AnalysisHistoryDTO> getAllAnalysisHistoryByUserId(int userId);

    /**
     * 특정 분석 이력을 수정합니다.
     * @param id 수정할 분석 이력 ID
     * @param requestDTO 수정할 정보가 담긴 DTO
     * @return 수정된 분석 이력 DTO
     */
    AnalysisHistoryDTO updateAnalysisHistory(int id, AnalysisHistoryDTO requestDTO);

    /**
     * 특정 분석 이력을 삭제합니다.
     * @param id 삭제할 분석 이력 ID
     */
    void deleteAnalysisHistory(int id);

    default AnalysisHistory DTOtoEntity(AnalysisHistoryDTO dto, User user) {
        String feedbackJson = null;
        String measurementsJson = null;
        String recommendedExerciseJson = null;
        try {
            if (dto.getFeedback() != null) {
                feedbackJson = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(dto.getFeedback());
            }
            if (dto.getMeasurements() != null) {
                measurementsJson = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(dto.getMeasurements());
            }
            if (dto.getRecommendedExercise() != null) {
                recommendedExerciseJson = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(dto.getRecommendedExercise());
            }
        } catch (Exception e) {
            // 변환 실패 시 null 유지
        }
        return AnalysisHistory.builder()
                .user(user)
                .spineCurvScore(dto.getSpineCurvScore())
                .spineScolScore(dto.getSpineScolScore())
                .pelvicScore(dto.getPelvicScore())
                .neckScore(dto.getNeckScore())
                .shoulderScore(dto.getShoulderScore())
                .frontImageUrl(dto.getFrontImageUrl())
                .sideImageUrl(dto.getSideImageUrl())
                .diagnosis(dto.getDiagnosis())
                .radarChartUrl(dto.getRadarChartUrl())
                .feedback(feedbackJson)
                .measurements(measurementsJson)
                .recommendedExercise(recommendedExerciseJson)
                .build();
    }

    default AnalysisHistoryDTO entityToDTO(AnalysisHistory history) {
        Map<String, Object> feedbackMap = null;
        Map<String, Object> measurementsMap = null;
        Map<String, Object> recommendedExerciseMap = null;
        try {
            if (history.getFeedback() != null) {
                feedbackMap = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue(history.getFeedback(), Map.class);
            }
            if (history.getMeasurements() != null) {
                measurementsMap = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue(history.getMeasurements(), Map.class);
            }
            if (history.getRecommendedExercise() != null) {
                recommendedExerciseMap = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue(history.getRecommendedExercise(), Map.class);
            }
        } catch (Exception e) {
            // 변환 실패 시 null 유지
        }
        return AnalysisHistoryDTO.builder()
                .id(history.getId())
                .userId(history.getUser().getId())
                .spineCurvScore(history.getSpineCurvScore())
                .spineScolScore(history.getSpineScolScore())
                .pelvicScore(history.getPelvicScore())
                .neckScore(history.getNeckScore())
                .shoulderScore(history.getShoulderScore())
                .frontImageUrl(history.getFrontImageUrl())
                .sideImageUrl(history.getSideImageUrl())
                .createdAt(history.getCreatedAt())
                .diagnosis(history.getDiagnosis())
                .radarChartUrl(history.getRadarChartUrl())
                .feedback(feedbackMap)
                .measurements(measurementsMap)
                .recommendedExercise(recommendedExerciseMap)
                .build();
    }
}
