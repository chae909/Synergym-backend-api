package org.synergym.backendapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.synergym.backendapi.dto.RecommendationPayloadDTO;
import org.synergym.backendapi.dto.RecommendationResponseDTO;
import org.synergym.backendapi.dto.ExerciseDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationGraphClient {
    private final WebClient webClient = WebClient.create("http://localhost:8000"); // Python 서버 주소

    public RecommendationResponseDTO fetchRecommendations(RecommendationPayloadDTO payload) {
        log.info("AI 추천 서버에 요청을 보냅니다. Payload: {}", payload);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/workflow/recommend-exercises")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "user_id", payload.getUserId(),
                            "user_profile", payload.getUserProfile(),
                            "posture_analysis", payload.getPostureAnalysis(),
                            "exercise_history", payload.getExerciseHistory(),
                            "liked_exercises", payload.getLikedExercises(),
                            "user_routines", payload.getUserRoutines()
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("AI 서버로부터 응답을 받았습니다: {}", response);

            return RecommendationResponseDTO.builder()
                    .message((String) response.get("message"))
                    .recommendations(convertToExerciseDTOList((List<Map<String, Object>>) response.get("recommendations")))
                    .reason((String) response.get("reason"))
                    .build();

        } catch (WebClientResponseException e) {
            log.error("AI 서버와의 통신 중 오류 발생: 상태 코드 {}, 메시지: {}",
                    e.getStatusCode(), e.getMessage());
            throw new RuntimeException("AI 서버와의 통신 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("RecommendationGraphClient에서 예상치 못한 오류 발생: {}", e.getMessage());
            throw new RuntimeException("운동 추천 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private List<ExerciseDTO> convertToExerciseDTOList(List<Map<String, Object>> recommendations) {
        if (recommendations == null) {
            return Collections.emptyList();
        }

        return recommendations.stream()
                .map(map -> ExerciseDTO.builder()
                        .id(map.get("id") != null ? ((Number) map.get("id")).intValue() : null)
                        .name((String) map.get("name"))
                        .category((String) map.get("category"))
                        .description((String) map.get("description"))
                        .difficulty((String) map.get("difficulty"))
                        .posture((String) map.get("posture"))
                        .bodyPart((String) map.get("bodyPart"))
                        .thumbnailUrl((String) map.get("thumbnailUrl"))
                        .build())
                .collect(Collectors.toList());
    }
}