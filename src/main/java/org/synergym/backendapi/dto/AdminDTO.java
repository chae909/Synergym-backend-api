package org.synergym.backendapi.dto;

import java.time.LocalDate;
import java.util.List;

public class AdminDTO {

    // 대시보드 전체 데이터를 담는 DTO
    public record DashboardResponse(
            StatsDto stats,
            GenderAnalysisDto genderAnalysis,
            List<AgeGroupAnalysisDTO> ageGroupAnalysis,
            List<PopularExerciseDto> popularByLikes,
            List<PopularExerciseDto> popularByRoutine
    ) {
        public record StatsDto(long totalMembers, long totalPosts, long totalAnalysis, WeeklyActiveUsersDto weeklyActiveUsers) {}
        public record WeeklyActiveUsersDto(long value, double change) {}
        public record GenderAnalysisDto(double male, double female, double maxScore) {}
        public record PopularExerciseDto(String name, int count) {}

        // 나이대별 분석 DTO
        public record AgeGroupAnalysisDTO(String ageGroup, double averageScore) {}
    }

    // 회원 정보 DTO (관리자 페이지용)
    public record MemberResponse(
            int id,
            String name,
            String email,
            LocalDate lastModified,
            LocalDate signedUp,
            String goal,
            LocalDate birthDate,
            String gender
    ) {}
}
