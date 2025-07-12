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

    // 세션 관리 헬퍼 메서드 추가
    private String getOrCreateSession(Integer userId, String sessionId) {
        // 기존 활성 세션이 있으면 재사용
        String activeSession = chatbotRedisService.getActiveSession(userId);
        if (activeSession != null && !activeSession.isEmpty()) {
            log.info("기존 활성 세션 재사용 - userId: {}, sessionId: {}", userId, activeSession);
            return activeSession;
        }
        
        // 새로운 세션 생성
        String newSessionId = chatbotRedisService.generateSessionId();
        chatbotRedisService.setActiveSession(userId, newSessionId);
        log.info("새 세션 생성 - userId: {}, sessionId: {}", userId, newSessionId);
        return newSessionId;
    }

    // 챗봇 메시지 전송
    @PostMapping("/send")
    public ResponseEntity<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO request) {
        try {
            Integer userId = request.getUserId();
            String sessionId = request.getSessionId();
            String userMessage = request.getMessage();
            
            // 세션 관리 통합
            sessionId = getOrCreateSession(userId, sessionId);
            
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

    // 초기 메시지 저장
    @PostMapping("/init-message")
    public ResponseEntity<ChatResponseDTO> saveInitialMessage(@RequestBody ChatRequestDTO request) {
        try {
            Integer userId = request.getUserId();
            String sessionId = request.getSessionId();
            String messageType = request.getMessage(); // "video" 또는 "consult"
            
            // 세션 관리 통합
            sessionId = getOrCreateSession(userId, sessionId);
            
            // 기존 대화 내역 확인
            List<ChatMessageDTO> existingHistory = chatbotRedisService.getChatHistory(userId, sessionId);
            
            // 대화 내역이 없을 때만 초기 메시지 추가
            if (existingHistory.isEmpty()) {
                // 초기 메시지 생성
                String initialMessage = generateInitialMessage(messageType);
                
                // Redis에 초기 메시지만 저장 (사용자 메시지 없이)
                chatbotRedisService.saveInitialMessage(userId, sessionId, initialMessage, messageType);
                
                return ResponseEntity.ok(new ChatResponseDTO("bot", initialMessage, sessionId));
            } else {
                // 이미 대화 내역이 있으면 기존 메시지 중 마지막 bot 메시지 반환
                ChatMessageDTO lastBotMessage = existingHistory.stream()
                    .filter(msg -> "bot".equals(msg.getType()))
                    .reduce((first, second) -> second)
                    .orElse(new ChatMessageDTO("bot", "안녕하세요! 무엇을 도와드릴까요?"));
                
                return ResponseEntity.ok(new ChatResponseDTO("bot", lastBotMessage.getContent(), sessionId));
            }
            
        } catch (Exception e) {
            log.error("초기 메시지 저장 실패", e);
            return ResponseEntity.badRequest().body(new ChatResponseDTO("error", "초기 메시지 처리 중 오류가 발생했습니다.", null));
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
            // 세션이 없으면 빈 문자열 반환 (null 대신)
            if (sessionId == null || sessionId.isEmpty()) {
                return ResponseEntity.ok("");
            }
            return ResponseEntity.ok(sessionId);
        } catch (Exception e) {
            log.error("활성 세션 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 강제 메시지 추가 (대화 내역과 상관없이)
    @PostMapping("/force-message")
    public ResponseEntity<ChatResponseDTO> forceAddMessage(@RequestBody ChatRequestDTO request) {
        try {
            Integer userId = request.getUserId();
            String sessionId = request.getSessionId();
            String messageType = request.getMessage(); // "video" or "consult"
            String content = request.getContent(); // 실제 메시지 내용
            String videoUrl = request.getVideoUrl(); // (선택) 영상 URL

            log.info("/force-message called: userId={}, sessionId={}, type={}, content={}, videoUrl={}", userId, sessionId, messageType, content, videoUrl);

            // 세션 관리 통합
            sessionId = getOrCreateSession(userId, sessionId);

            // 메시지 생성
            String messageToSave = content;

            // Redis에 무조건 메시지 추가
            chatbotRedisService.forceAddMessage(userId, sessionId, messageToSave, messageType, videoUrl);

            return ResponseEntity.ok(new ChatResponseDTO("bot", messageToSave, sessionId));
        } catch (Exception e) {
            log.error("강제 메시지 추가 실패", e);
            return ResponseEntity.badRequest().body(new ChatResponseDTO("error", "메시지 추가 중 오류", null));
        }
    }

    // 더미 AI 응답 생성
    private String generateDummyResponse(String userMessage) {
        if (userMessage.contains("운동") || userMessage.contains("추천")) {
            return "운동 추천을 도와드릴게요! 어떤 부위를 집중적으로 운동하고 싶으신가요?";
        } else if (userMessage.contains("자세")) {
            return "자세 교정에 대해 궁금하신 점이 있으시면 언제든 물어보세요!";
        } else if (userMessage.contains("루틴")) {
            return "루틴에 추가하시겠습니까? 어떤 운동을 추가하고 싶으신가요?";
        } else {
            return "운동이나 자세 교정에 대해 궁금한 점이 있으시면 언제든 물어보세요.";
        }
    }

    // 초기 메시지 생성
    private String generateInitialMessage(String messageType) {
        // 타입이 없거나 알 수 없는 경우, 기본 인사
        if (messageType == null || messageType.isEmpty()) {
            return "안녕하세요! 무엇을 도와드릴까요?";
        }
        
        // video나 consult 타입일 때도 기본 인사 메시지 반환
        if ("video".equals(messageType) || "consult".equals(messageType)) {
            return "안녕하세요! 무엇을 도와드릴까요?";
        }
        
        // 기타 알 수 없는 타입의 경우에도 기본 인사
        return "안녕하세요! 무엇을 도와드릴까요?";
    }
    
}
