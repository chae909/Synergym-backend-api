package org.synergym.backendapi.dto;

/**
 * FastAPI 감성 분석 서비스에 요청을 보낼 때 사용할 DTO입니다.
 * @param memo 분석할 텍스트 메모
 */
public record EmotionAnalysisRequest(String memo) {
}