package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.AdminDTO;
import java.util.List;
import org.synergym.backendapi.dto.UserSignupStatsResponse;

public interface AdminService {
    /**
     * 관리자 대시보드에 필요한 전체 통계 데이터 조회
     * @return 대시보드 응답 객체
     */
    AdminDTO.DashboardResponse getDashboardData();

    /**
     * 모든 회원에 대한 상세 정보를 조회
     * @return 회원 정보 리스트
     */
    List<AdminDTO.MemberResponse> getAllMembers();

    /**
     * 분석 횟수 분포에 대한 통계 데이터 조회
     * 성별/연령대/분석횟수별 유저 수 통계 등 시각화용 데이터 제공
     * @return 분석 분포 응답 객체
     */
    AdminDTO.DashboardResponse.AnalysisDistributionResponse getAnalysisDistributionData();

    /**
     * 연도별 월/주차 기준 사용자 가입 통계 조회
     * @param year 대상 연도 (예: 2024)
     * @return 사용자 가입 통계 응답 객체
     */
    UserSignupStatsResponse getUserSignupStats(int year);

}