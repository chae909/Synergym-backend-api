package org.synergym.backendapi.dto;

import java.time.LocalDate;
import java.util.List;

// 관리자 페이지 관련 DTO
public class AdminDTO {

    // 대시보드 전체 데이터를 담는 DTO
    public record DashboardResponse(
            StatsDto stats,
            GenderAnalysisDto genderAnalysis,
            List<AgeGroupAnalysisDTO> ageGroupAnalysis,
            List<PopularExerciseDto> popularByLikes,
            List<PopularExerciseDto> popularByRoutine,
            List<PopularPostDto> popularByViews,
            List<PopularPostDto> popularByComments,
            List<PopularPostDto> popularByPostLikes
    ) {
        public record StatsDto(long totalMembers, long totalPosts, long totalAnalysis, WeeklyActiveUsersDto weeklyActiveUsers) {}
        public record WeeklyActiveUsersDto(long value, double change) {}
        public record GenderAnalysisDto(double male, double female, double maxScore, long maleCount, long femaleCount) {}
        public record PopularExerciseDto(String name, int count) {}

        // 나이대별 분석 DTO
        public record AgeGroupAnalysisDTO(String ageGroup, double averageScore, long count) {}

        // 성별 분석횟수 분포
        public record GenderDistribution(String gender, String analysisCount, int userCount){}

        // 나이대별 분석횟수 분포
        public record AgeGroupDistribution(String ageGroup, String analysisCount, int userCount){}

        // 분포 응답용 DTO
        public record AnalysisDistributionResponse(List<DashboardResponse.GenderDistribution> genderDistribution, List<AgeGroupDistribution> ageGroupDistribution){}

        // 인기 게시글 DTO
        public record PopularPostDto(String title, int count, String categoryName, int postId) {}
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
