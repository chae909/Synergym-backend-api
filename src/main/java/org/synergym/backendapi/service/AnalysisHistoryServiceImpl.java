package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.AnalysisHistoryDTO;
import org.synergym.backendapi.entity.AnalysisHistory;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.AnalysisHistoryRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisHistoryServiceImpl implements AnalysisHistoryService {

    private final AnalysisHistoryRepository analysisHistoryRepository;
    private final UserRepository userRepository;

    private User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private AnalysisHistory findAnalysisHistoryById(int id) {
        return analysisHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.HISTORY_NOT_FOUND));
    }

    @Override
    @Transactional
    public AnalysisHistoryDTO createAnalysisHistory(AnalysisHistoryDTO requestDTO, int userId) {
        User user = findUserById(userId);
        AnalysisHistory newHistory = DTOtoEntity(requestDTO, user);
        AnalysisHistory savedHistory = analysisHistoryRepository.save(newHistory);
        return entityToDTO(savedHistory);
    }

    @Override
    public AnalysisHistoryDTO getAnalysisHistoryById(int id) {
        return entityToDTO(findAnalysisHistoryById(id));
    }

    @Override
    public List<AnalysisHistoryDTO> getAllAnalysisHistoryByUserId(int userId) {
        User user = findUserById(userId);
        return analysisHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnalysisHistoryDTO updateAnalysisHistory(int id, AnalysisHistoryDTO requestDTO) {
        AnalysisHistory history = findAnalysisHistoryById(id);

        history.updateSpineCurvScore(requestDTO.getSpineCurvScore());
        history.updateSpineScolScore(requestDTO.getSpineScolScore());
        history.updatePelvicScore(requestDTO.getPelvicScore());
        history.updateNeckScore(requestDTO.getNeckScore());
        history.updateShoulderScore(requestDTO.getShoulderScore());
        history.updateFrontImageUrl(requestDTO.getFrontImageUrl());
        history.updateSideImageUrl(requestDTO.getSideImageUrl());

        return entityToDTO(history);
    }

    @Override
    @Transactional
    public void deleteAnalysisHistory(int id) {
        AnalysisHistory history = analysisHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.HISTORY_NOT_FOUND));

        history.softDelete();
    }
}
