package org.synergym.backendapi.dto;

import lombok.Data;
 
@Data
public class AnalysisRequestDTO {
    private String imageUrl; // Cloudinary 이미지 URL
    private String mode;     // 분석 모드 (예: "front")
} 