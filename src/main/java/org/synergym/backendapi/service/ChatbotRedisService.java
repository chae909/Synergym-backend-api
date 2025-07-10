package org.synergym.backendapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.dto.ChatMessageDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotRedisService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // 대화 메시지 저장 (24시간 TTL)
    public void saveChatMessage(Integer userId, String sessionId, String userMessage, String aiResponse) {
        try {
            String key = "chat:session:" + userId + ":" + sessionId;
            
            // 기존 대화 조회
            List<ChatMessageDTO> messages = getChatHistory(userId, sessionId);
            
            // 새 메시지 추가
            messages.add(new ChatMessageDTO("user", userMessage));
            messages.add(new ChatMessageDTO("bot", aiResponse));
            
            // 최대 50개 메시지로 제한
            if (messages.size() > 50) {
                messages = messages.subList(messages.size() - 50, messages.size());
            }
            
            // Redis에 저장 (24시간 TTL)
            String jsonData = objectMapper.writeValueAsString(messages);
            redisTemplate.opsForValue().set(key, jsonData, Duration.ofHours(24));
            
            log.info("챗봇 메시지 저장 완료 - userId: {}, sessionId: {}, 메시지 수: {}", 
                    userId, sessionId, messages.size());
                    
        } catch (Exception e) {
            log.error("챗봇 메시지 저장 실패 - userId: {}, sessionId: {}", userId, sessionId, e);
        }
    }

    // 대화 기록 조회
    public List<ChatMessageDTO> getChatHistory(Integer userId, String sessionId) {
        try {
            String key = "chat:session:" + userId + ":" + sessionId;
            String data = redisTemplate.opsForValue().get(key);
            
            if (data != null) {
                return objectMapper.readValue(data, new TypeReference<List<ChatMessageDTO>>() {});
            }
        } catch (Exception e) {
            log.error("챗봇 기록 조회 실패 - userId: {}, sessionId: {}", userId, sessionId, e);
        }
        
        return new ArrayList<>();
    }

    // 새로운 세션 ID 생성
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    // 사용자별 활성 세션 저장
    public void setActiveSession(Integer userId, String sessionId) {
        String key = "chat:active:" + userId;
        redisTemplate.opsForValue().set(key, sessionId, Duration.ofHours(1));
    }

    // 사용자별 활성 세션 조회
    public String getActiveSession(Integer userId) {
        String key = "chat:active:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // 세션 삭제
    public void deleteSession(Integer userId, String sessionId) {
        String key = "chat:session:" + userId + ":" + sessionId;
        redisTemplate.delete(key);
    }

    // 사용자별 모든 세션 조회
    public List<String> getUserSessions(Integer userId) {
        String pattern = "chat:session:" + userId + ":*";
        return redisTemplate.keys(pattern).stream()
                .map(key -> key.split(":")[3])
                .toList();
    }
    
}
