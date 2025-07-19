package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.dto.GoalRequestDTO;
import org.synergym.backendapi.dto.GoalResponseDTO;

@Service
@RequiredArgsConstructor
public class GoalRecommendationService {

    private final GoalGraphClient goalGraphClient;

    /**
     * 요청 데이터를 받아 AI 클라이언트를 호출하고, 받은 결과를 반환합니다.
     * @param requestDTO 프론트엔드에서 받은 요청 데이터
     * @return AI가 생성한 목표 추천 DTO
     */
    public GoalResponseDTO generateGoals(GoalRequestDTO requestDTO) {
        return goalGraphClient.fetchGoals(requestDTO.getExerciseHistory(), requestDTO.getCoachPersona());
    }
}