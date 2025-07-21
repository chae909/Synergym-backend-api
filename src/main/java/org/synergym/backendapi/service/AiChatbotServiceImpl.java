package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.ChatResponseDTO;
import java.util.Map;

@Service
public class AiChatbotServiceImpl implements AiChatbotService {
    private final WebClient webClient = WebClient.create("http://localhost:8000"); // FastAPI 주소

    @Override
    public ChatResponseDTO callFastApiAiCoach(Map<String, Object> fastApiRequest) {
        return webClient.post()
                .uri("/ai-coach")
                .bodyValue(fastApiRequest)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();
    }

    @Override
    public ChatResponseDTO callFastApiYoutube(Map<String, Object> fastApiRequest, String type) {
        fastApiRequest.put("type", type);
        return webClient.post()
                .uri("/youtube")
                .bodyValue(fastApiRequest)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();
    }
} 