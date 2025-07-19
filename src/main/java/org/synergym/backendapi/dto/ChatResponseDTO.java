package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String type;  // "bot" 또는 "error"
    private String response;
    private String sessionId;
    private String videoUrl;
    private String videoTitle;
}
