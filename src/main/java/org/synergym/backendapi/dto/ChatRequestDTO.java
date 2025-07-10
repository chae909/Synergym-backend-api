package org.synergym.backendapi.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private Integer userId;
    private String sessionId;
    private String message;
    
}
