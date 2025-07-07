package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.dto.AdminDTO;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.AnalysisHistoryRepository;
import org.synergym.backendapi.repository.CategoryRepository;
import org.synergym.backendapi.repository.PostRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    // Repository 및 신규 Service 의존성 주입
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AnalysisHistoryRepository analysisHistoryRepository;
    private final ExerciseService exerciseService;

    @Override
    public AdminDTO.DashboardResponse getDashboardData() {
        // --- 1. 기본 통계 (Stats) 계산 ---
        long totalMembers = userRepository.count();
        long totalPosts = postRepository.count();
        // '총 분석 횟수'는 AnalysisHistory 테이블의 전체 레코드 수로 계산
        long totalAnalysis = analysisHistoryRepository.count();

        // 주간 활성 사용자 계산 (기존 로직 유지)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        LocalDateTime twoWeeksAgo = now.minusWeeks(2);
        long currentWeekActiveUsers = userRepository.countByUpdatedAtAfter(oneWeekAgo);
        long previousWeekActiveUsers = userRepository.countByUpdatedAtBetween(twoWeeksAgo, oneWeekAgo);
        double weeklyChange = (previousWeekActiveUsers > 0) ? ((double) (currentWeekActiveUsers - previousWeekActiveUsers) / previousWeekActiveUsers) * 100 : 0.0;

        AdminDTO.DashboardResponse.StatsDto stats = new AdminDTO.DashboardResponse.StatsDto(
                totalMembers, totalPosts, totalAnalysis,
                new AdminDTO.DashboardResponse.WeeklyActiveUsersDto(currentWeekActiveUsers, Double.parseDouble(String.format("%.1f", weeklyChange)))
        );

        // --- 2. 성별 분석 점수 계산 (개선된 버전) ---
        // Repository에서 성별 평균 점수 데이터를 직접 조회
        List<Object[]> results = analysisHistoryRepository.findAverageScoreByGender();

        results.forEach(r -> log.info("  - 성별: {}, 평균 점수: {}", r[0], r[1]));

        Map<String, Double> averageScoresByGender = results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Double) result[1]
                ));

        // getOrDefault로 안전하게 값 조회
        double maleAverageScore = averageScoresByGender.getOrDefault("MALE", 0.0);
        double femaleAverageScore = averageScoresByGender.getOrDefault("FEMALE", 0.0);

        // DTO 생성 시, 바로 반올림하여 할당
        AdminDTO.DashboardResponse.GenderAnalysisDto genderAnalysis = new AdminDTO.DashboardResponse.GenderAnalysisDto(
                roundToOneDecimal(maleAverageScore),   // 반올림 헬퍼 메소드 사용
                roundToOneDecimal(femaleAverageScore),  // 반올림 헬퍼 메소드 사용
                100.0 // 점수는 100점 만점으로 가정
        );

        // --- 2.1. 나이대별 분석 점수 계산 ---
        List<AdminDTO.DashboardResponse.AgeGroupAnalysisDTO> ageGroupAnalysis =
                analysisHistoryRepository.findAverageScoreByAgeGroup().stream()
                        .map(result -> new AdminDTO.DashboardResponse.AgeGroupAnalysisDTO(
                                (String) result[0], // ageGroup (e.g., "20대")
                                Double.parseDouble(String.format("%.1f", (Double) result[1])) // averageScore
                        ))
                        .collect(Collectors.toList());

        // --- 3. 인기 운동 데이터 조회 ---
        // 주입받은 ExerciseService의 메소드를 직접 호출
        List<ExerciseDTO> likesTop3 = exerciseService.getPopularExercisesByLikes(250);
        List<ExerciseDTO> routinesTop3 = exerciseService.getPopularExercisesByRoutines(250);

        // ExerciseDTO를 Dashboard DTO로 변환
        List<AdminDTO.DashboardResponse.PopularExerciseDto> popularByLikes = likesTop3.stream()
                .map(e -> new AdminDTO.DashboardResponse.PopularExerciseDto(e.getName(), e.getLikeCount().intValue()))
                .collect(Collectors.toList());

        List<AdminDTO.DashboardResponse.PopularExerciseDto> popularByRoutine = routinesTop3.stream()
                .map(e -> new AdminDTO.DashboardResponse.PopularExerciseDto(e.getName(), e.getRoutineCount().intValue()))
                .collect(Collectors.toList());

        return new AdminDTO.DashboardResponse(stats, genderAnalysis, ageGroupAnalysis, popularByLikes, popularByRoutine);
    }

    @Override
    public List<AdminDTO.MemberResponse> getAllMembers() {
        return userRepository.findAll().stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    // --- Entity to DTO 변환 헬퍼 메소드 ---

    private AdminDTO.MemberResponse toMemberResponse(User user) {
        return new AdminDTO.MemberResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUpdatedAt().toLocalDate(),
                user.getCreatedAt().toLocalDate(),
                user.getGoal(),
                user.getBirthday(),
                user.getGender()
        );
    }

    /**
     * double 값을 소수점 첫째 자리까지 반올림하는 헬퍼 메소드
     * @param value 반올림할 값
     * @return 반올림된 값
     */
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}