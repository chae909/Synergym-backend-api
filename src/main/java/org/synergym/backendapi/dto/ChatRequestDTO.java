package org.synergym.backendapi.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String type;      // "ai_coach" or "recommend_video"
    private Integer userId;
    private Integer historyId;
    private String sessionId;
    private String message;   // 사용자 입력 메시지
    private String content;   // 실제 메시지 내용 (ex. "OOO 운동을 추천드립니다...")
    private String videoUrl;  // (선택) 영상 URL
}
