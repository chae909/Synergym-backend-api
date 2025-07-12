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

    // 초기 메시지 저장 (영상 추천 또는 상담)
    public void saveInitialMessage(Integer userId, String sessionId, String initialMessage, String messageType) {
        try {
            String key = "chat:session:" + userId + ":" + sessionId;
            
            // 기존 대화 조회
            List<ChatMessageDTO> messages = getChatHistory(userId, sessionId);
            
            // 대화 내역이 없을 때만 초기 메시지 추가
            if (messages.isEmpty()) {
                // 초기 메시지만 추가 (사용자 메시지 없이)
                messages.add(new ChatMessageDTO("bot", initialMessage));
                
                // 최대 50개 메시지로 제한
                if (messages.size() > 50) {
                    messages = messages.subList(messages.size() - 50, messages.size());
                }
                
                // Redis에 저장 (24시간 TTL)
                String jsonData = objectMapper.writeValueAsString(messages);
                redisTemplate.opsForValue().set(key, jsonData, Duration.ofHours(24));
                
                log.info("초기 메시지 저장 완료 - userId: {}, sessionId: {}, messageType: {}", 
                        userId, sessionId, messageType);
            } else {
                log.info("대화 내역이 이미 존재하여 초기 메시지 저장 건너뜀 - userId: {}, sessionId: {}", 
                        userId, sessionId);
            }
                    
        } catch (Exception e) {
            log.error("초기 메시지 저장 실패 - userId: {}, sessionId: {}", userId, sessionId, e);
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
    
    // 대화 내역에 무조건 메시지 추가
    public void forceAddMessage(Integer userId, String sessionId, String content, String messageType, String videoUrl) {
        try {
            String key = "chat:session:" + userId + ":" + sessionId;
            List<ChatMessageDTO> messages = getChatHistory(userId, sessionId);

            if ("video".equals(messageType) && videoUrl != null) {
                messages.add(new ChatMessageDTO("bot", content, videoUrl));
            } else {
                messages.add(new ChatMessageDTO("bot", content));
            }

            // 최대 50개 제한
            if (messages.size() > 50) {
                messages = messages.subList(messages.size() - 50, messages.size());
            }

            String jsonData = objectMapper.writeValueAsString(messages);
            redisTemplate.opsForValue().set(key, jsonData, Duration.ofHours(24));
            log.info("강제 메시지 추가 완료 - userId: {}, sessionId: {}, messageType: {}, videoUrl: {}", userId, sessionId, messageType, videoUrl);
        } catch (Exception e) {
            log.error("강제 메시지 추가 실패 - userId: {}, sessionId: {}", userId, sessionId, e);
        }
    }
}
