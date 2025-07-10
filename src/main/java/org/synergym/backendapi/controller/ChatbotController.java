package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.ChatMessageDTO;
import org.synergym.backendapi.dto.ChatRequestDTO;
import org.synergym.backendapi.dto.ChatResponseDTO;
import org.synergym.backendapi.service.ChatbotRedisService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotRedisService chatbotRedisService;

    // 챗봇 메시지 전송
    @PostMapping("/send")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        try {
            Integer userId = request.getUserId();
            String sessionId = request.getSessionId();
            String userMessage = request.getMessage();
            
            // 세션이 없으면 새로 생성
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = chatbotRedisService.generateSessionId();
                chatbotRedisService.setActiveSession(userId, sessionId);
            }
            
            // AI 응답 생성 (현재는 더미 응답)
            String aiResponse = generateDummyResponse(userMessage);
            
            // Redis에 대화 저장
            chatbotRedisService.saveChatMessage(userId, sessionId, userMessage, aiResponse);
            
            return ResponseEntity.ok(new ChatResponseDTO("bot", aiResponse, sessionId));
            
        } catch (Exception e) {
            log.error("챗봇 메시지 처리 실패", e);
            return ResponseEntity.badRequest().body(new ChatResponseDTO("error", "메시지 처리 중 오류가 발생했습니다.", null));
        }
    }

    // 대화 기록 조회
    @GetMapping("/history/{userId}/{sessionId}")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Integer userId, 
                                                          @PathVariable String sessionId) {
        try {
            List<ChatMessageDTO> history = chatbotRedisService.getChatHistory(userId, sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("대화 기록 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 사용자별 활성 세션 조회
    @GetMapping("/active-session/{userId}")
    public ResponseEntity<String> getActiveSession(@PathVariable Integer userId) {
        try {
            String sessionId = chatbotRedisService.getActiveSession(userId);
            return ResponseEntity.ok(sessionId);
        } catch (Exception e) {
            log.error("활성 세션 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 더미 AI 응답 생성
    private String generateDummyResponse(String userMessage) {
        if (userMessage.contains("운동") || userMessage.contains("추천")) {
            return "운동 추천을 도와드릴게요! 어떤 부위를 집중적으로 운동하고 싶으신가요?";
        } else if (userMessage.contains("자세")) {
            return "자세 교정에 대해 궁금하신 점이 있으시면 언제든 물어보세요!";
        } else {
            return "안녕하세요! 운동이나 자세 교정에 대해 궁금한 점이 있으시면 언제든 물어보세요.";
        }
    }
    
}
