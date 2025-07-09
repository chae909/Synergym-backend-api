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
import org.synergym.backendapi.dto.UserSignupStatsResponse;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.temporal.WeekFields;
import java.util.Locale;

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

        // --- 2. 성별 분석 데이터 계산 (점수 및 횟수) ---
        // 2.1. 성별 평균 점수
        List<Object[]> scoreResults = analysisHistoryRepository.findAverageScoreByGender();
        Map<String, Double> averageScoresByGender = scoreResults.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Double) result[1]
                ));
        double maleAverageScore = averageScoresByGender.getOrDefault("MALE", 0.0);
        double femaleAverageScore = averageScoresByGender.getOrDefault("FEMALE", 0.0);

        // 2.2. 성별 분석 횟수
        List<Object[]> countResults = analysisHistoryRepository.countByGender();
        Map<String, Long> countsByGender = countResults.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
        long maleCount = countsByGender.getOrDefault("MALE", 0L);
        long femaleCount = countsByGender.getOrDefault("FEMALE", 0L);

        // 2.3. DTO 생성 (점수와 횟수 포함)
        AdminDTO.DashboardResponse.GenderAnalysisDto genderAnalysis = new AdminDTO.DashboardResponse.GenderAnalysisDto(
                roundToOneDecimal(maleAverageScore),
                roundToOneDecimal(femaleAverageScore),
                100.0, // 점수는 100점 만점으로 가정
                maleCount,
                femaleCount
        );

        // --- 3. 나이대별 분석 데이터 계산 (점수 및 횟수) ---
        // 3.1. 나이대별 분석 횟수 조회 후 Map으로 변환
        Map<String, Long> countsByAgeGroup = analysisHistoryRepository.countByAgeGroup().stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));

        // 3.2. 나이대별 평균 점수를 조회하며, 위에서 구한 횟수 Map을 사용하여 DTO 생성
        List<AdminDTO.DashboardResponse.AgeGroupAnalysisDTO> ageGroupAnalysis =
                analysisHistoryRepository.findAverageScoreByAgeGroup().stream()
                        .map(result -> {
                            String ageGroup = (String) result[0];
                            double averageScore = Double.parseDouble(String.format("%.1f", (Double) result[1]));
                            long count = countsByAgeGroup.getOrDefault(ageGroup, 0L); // 맵에서 횟수 조회
                            return new AdminDTO.DashboardResponse.AgeGroupAnalysisDTO(ageGroup, averageScore, count);
                        })
                        .collect(Collectors.toList());

        // --- 4. 인기 운동 데이터 조회 ---
        List<ExerciseDTO> likesTop3 = exerciseService.getPopularExercisesByLikes(250);
        List<ExerciseDTO> routinesTop3 = exerciseService.getPopularExercisesByRoutines(250);
        List<AdminDTO.DashboardResponse.PopularExerciseDto> popularByLikes = likesTop3.stream()
                .map(e -> new AdminDTO.DashboardResponse.PopularExerciseDto(e.getName(), e.getLikeCount().intValue()))
                .collect(Collectors.toList());
        List<AdminDTO.DashboardResponse.PopularExerciseDto> popularByRoutine = routinesTop3.stream()
                .map(e -> new AdminDTO.DashboardResponse.PopularExerciseDto(e.getName(), e.getRoutineCount().intValue()))
                .collect(Collectors.toList());

        // --- 5. 인기 게시글 데이터 조회 ---
        List<Object[]> viewsResults = postRepository.findPopularPostsByViews();
        List<AdminDTO.DashboardResponse.PopularPostDto> popularByViews = viewsResults.stream()
                .limit(10)
                .map(result -> new AdminDTO.DashboardResponse.PopularPostDto((String) result[0], ((Number) result[1]).intValue(), (String) result[2], ((Number) result[3]).intValue()))
                .collect(Collectors.toList());

        List<Object[]> commentsResults = postRepository.findPopularPostsByComments();
        List<AdminDTO.DashboardResponse.PopularPostDto> popularByComments = commentsResults.stream()
                .limit(10)
                .map(result -> new AdminDTO.DashboardResponse.PopularPostDto((String) result[0], ((Number) result[1]).intValue(), (String) result[2], ((Number) result[3]).intValue()))
                .collect(Collectors.toList());

        List<Object[]> likesResults = postRepository.findPopularPostsByLikes();
        List<AdminDTO.DashboardResponse.PopularPostDto> popularByPostLikes = likesResults.stream()
                .limit(10)
                .map(result -> new AdminDTO.DashboardResponse.PopularPostDto((String) result[0], ((Number) result[1]).intValue(), (String) result[2], ((Number) result[3]).intValue()))
                .collect(Collectors.toList());

        // --- 6. 최종 응답 생성 ---
        return new AdminDTO.DashboardResponse(stats, genderAnalysis, ageGroupAnalysis, popularByLikes, popularByRoutine,
                popularByViews, popularByComments, popularByPostLikes);
    }

    @Override
    public List<AdminDTO.MemberResponse> getAllMembers() {
        return userRepository.findAll().stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminDTO.DashboardResponse.AnalysisDistributionResponse getAnalysisDistributionData() {
        log.info("--- 분석 분포 데이터 서비스 로직 시작 ---");

        log.info("[1/4] 성별 분석 분포 데이터 조회를 시작합니다...");
        List<Object[]> genderResults = analysisHistoryRepository.findUserCountByAnalysisCountPerGender();
        log.info("  ▶ Raw DB 결과 (성별): 총 {}건", genderResults.size());

        if (!genderResults.isEmpty()) {
            Object[] firstRow = genderResults.get(0);
            log.info("  ▶ 첫 번째 행 (성별) Raw 데이터: {}, 타입: {}",
                    Arrays.toString(firstRow),
                    Arrays.stream(firstRow).map(o -> o.getClass().getName()).collect(Collectors.joining(", "))
            );
        }

        log.info("[2/4] 성별 분석 분포 데이터를 DTO로 가공합니다...");
        List<AdminDTO.DashboardResponse.GenderDistribution> genderList = genderResults.stream()
                .map(row -> {
                    String analysisCountGroup = (String) row[0]; // 분석 횟수 그룹
                    String gender = (String) row[1];             // 성별
                    int userCount = safeCastToInt(row[2]);       // 사용자 수

                    return new AdminDTO.DashboardResponse.GenderDistribution(gender, analysisCountGroup, userCount);
                }).collect(Collectors.toList());

        log.info("[3/4] 나이대별 분석 분포 데이터 조회를 시작합니다...");
        List<Object[]> ageResults = analysisHistoryRepository.findUserCountByAnalysisCountPerAgeGroup();
        log.info("  ▶ Raw DB 결과 (나이대별): 총 {}건", ageResults.size());

        if (!ageResults.isEmpty()) {
            Object[] firstRow = ageResults.get(0);
            log.info("  ▶ 첫 번째 행 (나이대별) Raw 데이터: {}, 타입: {}",
                    Arrays.toString(firstRow),
                    Arrays.stream(firstRow).map(o -> o.getClass().getName()).collect(Collectors.joining(", "))
            );
        }

        log.info("[4/4] 나이대별 분석 분포 데이터를 DTO로 가공합니다...");
        List<AdminDTO.DashboardResponse.AgeGroupDistribution> ageList = ageResults.stream()
                .map(row -> {
                    String analysisCountGroup = (String) row[0]; // 분석 횟수 그룹
                    String gender = (String) row[1];             // 성별
                    int userCount = safeCastToInt(row[2]);       // 사용자 수

                   return new AdminDTO.DashboardResponse.AgeGroupDistribution(gender, analysisCountGroup, userCount);
                }).collect(Collectors.toList());

        log.info("--- 분석 분포 데이터 서비스 로직 종료 ---");
        return new AdminDTO.DashboardResponse.AnalysisDistributionResponse(genderList, ageList);
    }

    @Override
    public UserSignupStatsResponse getUserSignupStats(int year) {
        // 전체 사용자 목록 조회
        List<User> users = userRepository.findAll();

        // 월별로 주차별 가입자 수를 저장할 맵 생성 (key: 월, value: 주차별 가입 수 배열[5칸])
        Map<Integer, int[]> monthWeekCounts = new java.util.HashMap<>();
        for (int m = 1; m <= 12; m++) monthWeekCounts.put(m, new int[5]);

        // Locale 기준 주차 계산 방식 설정 (한국 기준: 월요일 시작)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // 사용자 생성일 기준으로 연도, 월, 주차별 가입자 수 집계
        for (User user : users) {
            LocalDateTime created = user.getCreatedAt();

            // 입력된 연도와 다른 경우 무시
            if (created.getYear() != year) continue;

            int month = created.getMonthValue();  // 가입 월
            int weekOfMonth = created.toLocalDate().get(weekFields.weekOfMonth());  // 가입 주차 (1~5)

            // 1~5주차에 해당하는 경우만 카운팅 (6주차는 제외)
            if (weekOfMonth >= 1 && weekOfMonth <= 5) {
                monthWeekCounts.get(month)[weekOfMonth - 1]++;
            }
        }

        // 응답용 DTO 리스트 생성
        List<UserSignupStatsResponse.MonthlySignupStats> monthly = new java.util.ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            List<Integer> weekList = new java.util.ArrayList<>();

            // 각 월에 해당하는 주차별 카운트를 리스트로 변환
            for (int w : monthWeekCounts.get(m)) weekList.add(w);

            // DTO 생성: 월 번호 + 주차별 가입 수 리스트
            monthly.add(new UserSignupStatsResponse.MonthlySignupStats(m, weekList));
        }

        // 최종 응답 객체 생성
        return new UserSignupStatsResponse(monthly);
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

    private int safeCastToInt(Object value) {
        if (value == null) return 0;
        if (value instanceof BigInteger) return ((BigInteger) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Short) return ((Short) value).intValue();
        if (value instanceof Byte) return ((Byte) value).intValue();
        throw new IllegalArgumentException("지원되지 않는 타입: " + value.getClass().getName());
    }
}