package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.ChatResponseDTO;
import java.util.Map;

@Service
public class AiCoachClient {
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    public ChatResponseDTO sendAiCoachRequest(Map<String, Object> requestBody) {
        return webClient.post()
                .uri("/ai-coach")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();
    }
} 