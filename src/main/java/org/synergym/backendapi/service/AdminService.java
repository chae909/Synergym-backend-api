package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.AdminDTO;
import java.util.List;

public interface AdminService {
    AdminDTO.DashboardResponse getDashboardData();
    List<AdminDTO.MemberResponse> getAllMembers();
}