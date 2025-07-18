package org.synergym.backendapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PostureGraphClient {
    private final WebClient webClient = WebClient.create("http://localhost:8000"); // Python 서버 주소

    public Map<String, Object> analyzeWithGraph(String imageUrl, String mode) {
        Map<String, String> request = new HashMap<>();
        request.put("image_url", imageUrl);
        request.put("analysis_mode", mode);

        log.info("Sending request to Python server - Image URL: {}, Mode: {}", imageUrl, mode);

        try {
            Map<String, Object> result = webClient.post()
                    .uri("/analyze-graph")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            log.info("Received response from Python server: {}", result);
            return result;
        } catch (WebClientResponseException e) {
            log.error("Error communicating with Python server: Status {}, Message: {}, Code: {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Python 서버와의 통신 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in PostureGraphClient: {}", e.getMessage());
            throw new RuntimeException("자세 분석 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 