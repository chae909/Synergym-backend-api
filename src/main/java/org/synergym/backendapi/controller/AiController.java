package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.synergym.backendapi.dto.GoalRequestDTO;
import org.synergym.backendapi.dto.GoalResponseDTO;
import org.synergym.backendapi.service.GoalRecommendationService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final GoalRecommendationService goalRecommendationService;

    /**
     * 프론트엔드로부터 AI 목표 추천 요청을 받아 처리합니다.
     * @param requestDTO 사용자의 운동 기록과 코치 페르소나를 포함한 DTO
     * @return AI가 생성한 분석 결과 및 목표가 담긴 DTO
     */
    @PostMapping("/generate-goal")
    public ResponseEntity<GoalResponseDTO> generateGoal(@RequestBody GoalRequestDTO requestDTO) {
        GoalResponseDTO response = goalRecommendationService.generateGoals(requestDTO);
        return ResponseEntity.ok(response);
    }
}