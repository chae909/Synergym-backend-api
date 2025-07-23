package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.ChatResponseDTO;
import java.util.Map;

@Service
public class YoutubeClient {
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    public ChatResponseDTO sendYoutubeRequest(Map<String, Object> requestBody, String type) {
        requestBody.put("type", type);
        return webClient.post()
                .uri("/youtube")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();
    }
} 