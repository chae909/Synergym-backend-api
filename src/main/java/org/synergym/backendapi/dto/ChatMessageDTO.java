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
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public ChatMessageDTO(String type, String content) {
        this.type = type;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
    
}
