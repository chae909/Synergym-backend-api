package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.AnalysisHistoryDTO;
import org.synergym.backendapi.entity.AnalysisHistory;
import org.synergym.backendapi.entity.User;

import java.util.List;

public interface AnalysisHistoryService {

    AnalysisHistoryDTO createAnalysisHistory(AnalysisHistoryDTO requestDTO, int userId);
    AnalysisHistoryDTO getAnalysisHistoryById(int id);
    List<AnalysisHistoryDTO> getAllAnalysisHistoryByUserId(int userId);
    AnalysisHistoryDTO updateAnalysisHistory(int id, AnalysisHistoryDTO requestDTO);
    void deleteAnalysisHistory(int id);

    default AnalysisHistory DTOtoEntity(AnalysisHistoryDTO dto, User user) {
        return AnalysisHistory.builder()
                .user(user)
                .spineCurvScore(dto.getSpineCurvScore())
                .spineScolScore(dto.getSpineScolScore())
                .pelvicScore(dto.getPelvicScore())
                .neckScore(dto.getNeckScore())
                .shoulderScore(dto.getShoulderScore())
                .frontImageUrl(dto.getFrontImageUrl())
                .sideImageUrl(dto.getSideImageUrl())
                .build();
    }

    default AnalysisHistoryDTO entityToDTO(AnalysisHistory history) {
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
                .build();
    }
}
