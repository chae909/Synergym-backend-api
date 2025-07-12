package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String type;  // "user" 또는 "bot"
    private String content;
    private String videoUrl; // (선택) 영상 메시지라면 URL 저장
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public ChatMessageDTO(String type, String content) {
        this.type = type;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessageDTO(String type, String content, String videoUrl) {
        this.type = type;
        this.content = content;
        this.videoUrl = videoUrl;
        this.timestamp = LocalDateTime.now();
    }
    
}
