package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.ChatResponseDTO;
import java.util.Map;

@Service
public class AiCoachClient {
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    public ChatResponseDTO sendAiCoachRequest(Map<String, Object> requestBody) {
        // 1. 원본 JSON 응답을 String으로 받아서 출력
        String rawJson = webClient.post()
                .uri("/ai-coach")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 2. 기존대로 DTO로 변환해서 반환
        return webClient.post()
                .uri("/ai-coach")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();
    }
} 