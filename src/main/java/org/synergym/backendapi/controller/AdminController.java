package org.synergym.backendapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.synergym.backendapi.dto.AdminDTO;
import org.synergym.backendapi.service.AdminService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDTO.DashboardResponse> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    @GetMapping("/members")
    public ResponseEntity<List<AdminDTO.MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    @GetMapping("/analysis-distribution")
    public ResponseEntity<AdminDTO.DashboardResponse.AnalysisDistributionResponse> getAnalysisDistribution() {
        // [디버깅] API 호출 시작 로그
        log.info("✅✅✅ /api/admin/test 엔드포인트가 성공적으로 호출되었습니다! ✅✅✅");

        AdminDTO.DashboardResponse.AnalysisDistributionResponse response = adminService.getAnalysisDistributionData();

        return ResponseEntity.ok(response);
    }

}