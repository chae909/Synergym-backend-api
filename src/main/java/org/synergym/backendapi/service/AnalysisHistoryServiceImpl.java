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

    /**
     * 사용자 ID로 User 엔티티 조회
     * 존재하지 않으면 USER_NOT_FOUND 예외 발생
     */
    private User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 분석 이력 ID로 AnalysisHistory 엔티티 조회
     * 존재하지 않으면 HISTORY_NOT_FOUND 예외 발생
     */
    private AnalysisHistory findAnalysisHistoryById(int id) {
        return analysisHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.HISTORY_NOT_FOUND));
    }

    /**
     * 분석 이력 생성
     * - 사용자 유효성 검사
     * - DTO를 Entity로 변환 후 저장
     * - 저장된 Entity를 다시 DTO로 반환
     */
    @Override
    @Transactional
    public AnalysisHistoryDTO createAnalysisHistory(AnalysisHistoryDTO requestDTO, int userId) {
        User user = findUserById(userId);  // 사용자 유효성 확인
        AnalysisHistory newHistory = DTOtoEntity(requestDTO, user);  // DTO → Entity 변환
        AnalysisHistory savedHistory = analysisHistoryRepository.save(newHistory);  // 저장
        return entityToDTO(savedHistory);  // Entity → DTO 반환
    }

    /**
     * 분석 이력 ID로 조회 후 DTO로 변환하여 반환
     */
    @Override
    public AnalysisHistoryDTO getAnalysisHistoryById(int id) {
        return entityToDTO(findAnalysisHistoryById(id));
    }

    /**
     * 사용자 ID로 해당 사용자의 모든 분석 이력 조회 (최신순)
     */
    @Override
    public List<AnalysisHistoryDTO> getAllAnalysisHistoryByUserId(int userId) {
        User user = findUserById(userId);
        return analysisHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 분석 이력 업데이트
     * - ID로 기존 이력 조회
     * - 필드 값들을 부분 업데이트
     * - 업데이트된 엔티티를 DTO로 반환
     */
    @Override
    @Transactional
    public AnalysisHistoryDTO updateAnalysisHistory(int id, AnalysisHistoryDTO requestDTO) {
        AnalysisHistory history = findAnalysisHistoryById(id);  // 기존 이력 조회

        // 필드별 업데이트 (Setter 대신 커스텀 메서드 사용)
        history.updateSpineCurvScore(requestDTO.getSpineCurvScore());
        history.updateSpineScolScore(requestDTO.getSpineScolScore());
        history.updatePelvicScore(requestDTO.getPelvicScore());
        history.updateNeckScore(requestDTO.getNeckScore());
        history.updateShoulderScore(requestDTO.getShoulderScore());
        history.updateFrontImageUrl(requestDTO.getFrontImageUrl());
        history.updateSideImageUrl(requestDTO.getSideImageUrl());

        return entityToDTO(history);
    }

    /**
     * 분석 이력 삭제 (소프트 삭제 방식)
     * 실제 삭제하지 않고 softDelete 플래그만 변경
     */
    @Override
    @Transactional
    public void deleteAnalysisHistory(int id) {
        AnalysisHistory history = analysisHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.HISTORY_NOT_FOUND));

        history.softDelete();  // 실제 삭제 대신 상태만 변경
    }
}