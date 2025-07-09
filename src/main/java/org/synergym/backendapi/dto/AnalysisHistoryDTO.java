package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 분석 기록 관련 DTO
public class AnalysisHistoryDTO {

    private int id; // 분석기록 고유 id
    private int userId; // 분석기록 유저 id
    private int spineCurvScore; // 척추 만곡 점수
    private int spineScolScore; // 척추 측만 점수
    private int pelvicScore; // 골반 정렬 점수
    private int neckScore; // 목 자세 점수
    private int shoulderScore; // 어깨 정렬 점수
    private String frontImageUrl; // 정면 분석 이미지 url
    private String sideImageUrl; // 측면 분석 이미지 url
    private LocalDateTime createdAt; // 분석 생성 일시
}
