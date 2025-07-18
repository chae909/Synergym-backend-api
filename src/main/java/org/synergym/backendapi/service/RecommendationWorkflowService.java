package org.synergym.backendapi.service;

import org.springframework.stereotype.Service;
import org.synergym.backendapi.service.RecommendationGraphClient; // Python AI 서버와 통신하는 클라이언트 (가상)
import org.synergym.backendapi.dto.RecommendationPayloadDTO;
import org.synergym.backendapi.dto.RecommendationResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationWorkflowService {

    // Python으로 구현된 AI 추천 엔진과 통신하는 HTTP 클라이언트 (가정)
    private final RecommendationGraphClient recommendationGraphClient;

    /**
     * 페이로드 데이터를 기반으로 AI 추천 엔진을 호출하고 결과를 반환합니다.
     * @param payloadDTO 프론트에서 받은 사용자 데이터
     * @return AI가 생성한 추천 결과
     */
    public RecommendationResponseDTO getRecommendations(RecommendationPayloadDTO payloadDTO) {
        // 1. DTO를 AI 추천 엔진이 이해하는 형식으로 변환 (필요 시)
        // ...

        // 2. 외부 AI 엔진(Python 서버)에 추천 요청
        RecommendationResponseDTO responseFromAI = recommendationGraphClient.fetchRecommendations(payloadDTO);

        // 3. 받은 결과를 프론트엔드에 전달할 최종 형태로 가공 (필요 시)
        // 예: responseFromAI.setMessage("AI 추천이 성공적으로 완료되었습니다.");

        return responseFromAI;
    }
}