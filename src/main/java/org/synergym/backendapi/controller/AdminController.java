package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.AdminDTO;
import org.synergym.backendapi.service.AdminService;
import org.synergym.backendapi.dto.UserSignupStatsResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    // 대시보드 상단 통계 데이터 반환
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDTO.DashboardResponse> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    // 회원 관리 목록 반환
    @GetMapping("/members")
    public ResponseEntity<List<AdminDTO.MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    // 대시보드 하단 분포 데이터 반환
    @GetMapping("/analysis-distribution")
    public ResponseEntity<AdminDTO.DashboardResponse.AnalysisDistributionResponse> getAnalysisDistribution() {
        AdminDTO.DashboardResponse.AnalysisDistributionResponse response = adminService.getAnalysisDistributionData();

        return ResponseEntity.ok(response);
    }

    // 사용자 가입 통계 조회(특정 연도의 월별 회원가입 현황을 반환환)
    @GetMapping("/user-signup-stats")
    public ResponseEntity<UserSignupStatsResponse> getUserSignupStats(@RequestParam int year) {
        return ResponseEntity.ok(adminService.getUserSignupStats(year));
    }


}