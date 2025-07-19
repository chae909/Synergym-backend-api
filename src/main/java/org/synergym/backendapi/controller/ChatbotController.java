package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ChatRequestDTO;
import org.synergym.backendapi.dto.ChatResponseDTO;
import org.synergym.backendapi.dto.ChatMessageDTO;
import org.synergym.backendapi.service.AiChatbotService;
import org.synergym.backendapi.service.ChatbotRedisService;

import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final AiChatbotService aiChatbotService;
    private final ChatbotRedisService chatbotRedisService;

    // 메시지 전송 + FastAPI 호출 + Redis 저장
    @PostMapping("/send")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO requestDTO) {
        Integer userId = requestDTO.getUserId();
        String sessionId = chatbotRedisService.getActiveSession(userId);
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = chatbotRedisService.generateSessionId();
            chatbotRedisService.setActiveSession(userId, sessionId);
        }

        // 1. 사용자 메시지 Redis에 저장
        if (requestDTO.getMessage() != null && !requestDTO.getMessage().isEmpty()) {
            chatbotRedisService.saveChatMessage(userId, sessionId, requestDTO.getMessage(), null);
        }

        // 2. FastAPI 호출
        ChatResponseDTO aiResponse = aiChatbotService.callFastApi(requestDTO);

        // 3. AI 응답 Redis에 저장
        if (aiResponse.getResponse() != null && !aiResponse.getResponse().isEmpty()) {
            chatbotRedisService.saveChatMessage(userId, sessionId, null, aiResponse.getResponse());
        }

        // 4. 응답 반환 (세션ID도 필요하면 포함)
        aiResponse.setSessionId(sessionId);
        return ResponseEntity.ok(aiResponse);
    }

    // 대화 이력 조회 (현재 활성 세션)
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(@PathVariable Integer userId) {
        String sessionId = chatbotRedisService.getActiveSession(userId);
        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<ChatMessageDTO> history = chatbotRedisService.getChatHistory(userId, sessionId);
        return ResponseEntity.ok(history != null ? history : List.of());
    }

    // 대화 이력 조회 (특정 세션)
    @GetMapping("/history/{userId}/{sessionId}")
    public ResponseEntity<List<ChatMessageDTO>> getHistoryBySession(
            @PathVariable Integer userId,
            @PathVariable String sessionId) {
        List<ChatMessageDTO> history = chatbotRedisService.getChatHistory(userId, sessionId);
        return ResponseEntity.ok(history != null ? history : List.of());
    }

    // 세션ID 조회
    @GetMapping("/active-session/{userId}")
    public ResponseEntity<String> getActiveSession(@PathVariable Integer userId) {
        String sessionId = chatbotRedisService.getActiveSession(userId);
        return ResponseEntity.ok(sessionId != null ? sessionId : "");
    }
}
