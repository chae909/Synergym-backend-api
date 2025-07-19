package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.dto.GoalResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalGraphClient {
    private final AchievementService achievementService;
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    public GoalResponseDTO fetchGoals(List<ExerciseLogDTO> exerciseHistory, String coachPersona
    ) {
        log.info("AI 목표 추천 서버에 요청을 보냅니다. 페르소나: {}", coachPersona);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("exercise_history", exerciseHistory);
        requestBody.put("coach_persona", coachPersona);


        try {
            // ▼▼▼ 변경점: 응답을 Map이 아닌 GoalResponseDTO.class로 직접 받도록 변경 ▼▼▼
            // 이렇게 하면 Jackson 라이브러리가 JSON 응답을 DTO 구조에 맞춰 자동으로 변환해줍니다.
            GoalResponseDTO response = webClient.post()
                    .uri("/workflow/generate-goal") // main.py의 APIRouter 경로에 맞게 수정
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GoalResponseDTO.class) // Map.class -> GoalResponseDTO.class
                    .block();

            log.info("AI 목표 추천 서버로부터 응답을 성공적으로 변환했습니다: {}", response);

            if (response.getGeneratedBadge() != null) {
                achievementService.awardBadgeToUser(
                        response.getUserId(),
                        response.getGeneratedBadge().getName(),
                        response.getGeneratedBadge().getDescription()
                );
            }

            return response;

        } catch (WebClientResponseException e) {
            log.error("AI 서버와의 통신 중 오류 발생: 상태 코드 {}, 메시지: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI 서버와의 통신 중 오류가 발생했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("GoalGraphClient에서 예상치 못한 오류 발생: {}", e.getMessage(), e); // 스택 트레이스 포함
            throw new RuntimeException("AI 목표 추천 중 예상치 못한 오류가 발생했습니다.");
        }
    }
}