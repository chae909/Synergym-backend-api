package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.AdminDTO;
import java.util.List;
import org.synergym.backendapi.dto.UserSignupStatsResponse;

public interface AdminService {
    AdminDTO.DashboardResponse getDashboardData();
    List<AdminDTO.MemberResponse> getAllMembers();
    AdminDTO.DashboardResponse.AnalysisDistributionResponse getAnalysisDistributionData();
    UserSignupStatsResponse getUserSignupStats(int year);

}