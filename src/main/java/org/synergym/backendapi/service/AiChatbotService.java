package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.ChatResponseDTO;
import java.util.Map;

public interface AiChatbotService {
    ChatResponseDTO callFastApiAiCoach(Map<String, Object> fastApiRequest);
    ChatResponseDTO callFastApiYoutube(Map<String, Object> fastApiRequest, String type);
} 