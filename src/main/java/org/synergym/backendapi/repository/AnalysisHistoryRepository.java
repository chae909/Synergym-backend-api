package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.synergym.backendapi.entity.AnalysisHistory;

import java.util.List;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Integer> {

    List<AnalysisHistory> findByUserIdOrderByCreatedAtDesc(int userId);

    /**
     * 성별에 따른 평균 분석 점수를 계산합니다.
     * 각 분석 기록의 모든 부위 점수(5개)의 평균을 낸 뒤, 그 값들을 성별로 그룹화하여 다시 평균을 냅니다.
     * @return Object 배열 리스트, 각 배열은 [String gender, Double averageScore] 형태입니다.
     */
    @Query("SELECT ah.user.gender, AVG((ah.spineCurvScore + ah.spineScolScore + ah.pelvicScore + ah.neckScore + ah.shoulderScore) / 5.0) " +
            "FROM AnalysisHistory ah " +
            "WHERE ah.user.gender IS NOT NULL " +
            "GROUP BY ah.user.gender")
    List<Object[]> findAverageScoreByGender();

    /**
     * 나이대별 평균 분석 점수를 계산합니다.
     * 사용자의 생년월일을 기준으로 나이를 계산하고, 10대, 20대, 30대 등으로 그룹화합니다.
     * @return Object 배열 리스트, 각 배열은 [String ageGroup, Double averageScore] 형태입니다.
     */
    @Query("SELECT " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END, " +
            "  AVG((ah.spineCurvScore + ah.spineScolScore + ah.pelvicScore + ah.neckScore + ah.shoulderScore) / 5.0) " +
            "FROM AnalysisHistory ah JOIN ah.user u " +
            "WHERE u.birthday IS NOT NULL " +
            "GROUP BY " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END " +
            "ORDER BY " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END")
    List<Object[]> findAverageScoreByAgeGroup();

    /**
     * 성별에 따른 총 분석 횟수를 계산합니다.
     * @return Object 배열 리스트, 각 배열은 [String gender, Long count] 형태입니다.
     */
    @Query("SELECT ah.user.gender, COUNT(ah) " +
            "FROM AnalysisHistory ah " +
            "WHERE ah.user.gender IS NOT NULL " +
            "GROUP BY ah.user.gender")
    List<Object[]> countByGender();

    /**
     * 나이대별 총 분석 횟수를 계산합니다.
     * @return Object 배열 리스트, 각 배열은 [String ageGroup, Long count] 형태입니다.
     */
    @Query("SELECT " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END, " +
            "  COUNT(ah) " +
            "FROM AnalysisHistory ah JOIN ah.user u " +
            "WHERE u.birthday IS NOT NULL " +
            "GROUP BY " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END " +
            "ORDER BY " +
            "  CASE " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 20 THEN '10대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 30 THEN '20대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 40 THEN '30대' " +
            "    WHEN (YEAR(CURRENT_DATE) - YEAR(u.birthday)) < 50 THEN '40대' " +
            "    ELSE '50대 이상' " +
            "  END")
    List<Object[]> countByAgeGroup();

    /**
     * 성별(gender)과 분석 횟수(analysis_count)에 따른 사용자 수 통계 조회
     *
     * 분석 횟수를 0회, 1회, 2회, 3회 이상 그룹으로 나눈 후 성별 기준으로 유저 수 집계
     */
    @Query(
            value = "WITH user_analysis AS ( " +
                    "  SELECT " +
                    "    u.user_id, " +
                    "    u.gender, " +
                    "    COUNT(a.analysis_id) AS analysis_count " +  // 사용자별 분석 이력 수 카운트
                    "  FROM Users u " +
                    "  LEFT JOIN Analysis_History a ON u.user_id = a.user_id " +  // 분석 기록이 없을 수도 있으므로 LEFT JOIN 사용
                    "  WHERE u.gender IS NOT NULL " +  // 성별 정보가 있는 사용자만 대상
                    "  GROUP BY u.user_id, u.gender " +
                    ") " +
                    "SELECT " +
                    "  CASE " +  // 분석 횟수에 따른 그룹 라벨링
                    "    WHEN analysis_count = 0 THEN '0회' " +
                    "    WHEN analysis_count = 1 THEN '1회' " +
                    "    WHEN analysis_count = 2 THEN '2회' " +
                    "    ELSE '3회 이상' " +
                    "  END AS analysis_count_group, " +
                    "  gender, " +
                    "  COUNT(*) AS user_count " +  // 동일 그룹 내 사용자 수 집계
                    "FROM user_analysis " +
                    "GROUP BY analysis_count, gender " +  // 그룹핑 기준: 분석 횟수, 성별
                    "ORDER BY " +
                    "  CASE " +  // 분석 횟수 정렬 우선순위 (숫자 순으로 보기 좋게)
                    "    WHEN analysis_count = 0 THEN 0 " +
                    "    WHEN analysis_count = 1 THEN 1 " +
                    "    WHEN analysis_count = 2 THEN 2 " +
                    "    ELSE 3 " +
                    "  END, " +
                    "  gender",
            nativeQuery = true
    )
    List<Object[]> findUserCountByAnalysisCountPerGender();

    /**
     * 연령대(age_group)와 분석 횟수에 따른 사용자 수 통계 조회
     *
     * 생년(birthday) 기준으로 10대~50대 이상 그룹을 나누고,
     * 분석 횟수에 따라 다시 0/1/2/3회 이상으로 분류하여 사용자 수를 집계함
     */
    @Query(
            value = "WITH user_analysis AS ( " +
                    "  SELECT " +
                    "    u.user_id, " +
                    "    CASE " +
                    "      WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM u.birthday)) < 20 THEN '10대' " +
                    "      WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM u.birthday)) < 30 THEN '20대' " +
                    "      WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM u.birthday)) < 40 THEN '30대' " +
                    "      WHEN (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM u.birthday)) < 50 THEN '40대' " +
                    "      ELSE '50대 이상' " +
                    "    END AS age_group, " +  // 연령대 라벨 지정
                    "    COUNT(a.analysis_id) AS analysis_count " +  // 사용자별 분석 이력 수
                    "  FROM Users u " +
                    "  LEFT JOIN Analysis_History a ON u.user_id = a.user_id " +
                    "  WHERE u.birthday IS NOT NULL " +  // 생년 정보가 있는 사용자만 대상
                    "  GROUP BY u.user_id, age_group " +
                    ") " +
                    "SELECT " +
                    "  CASE " +
                    "    WHEN analysis_count = 0 THEN '0회' " +
                    "    WHEN analysis_count = 1 THEN '1회' " +
                    "    WHEN analysis_count = 2 THEN '2회' " +
                    "    ELSE '3회 이상' " +
                    "  END AS analysis_count_group, " +
                    "  age_group, " +
                    "  COUNT(*) AS user_count " +  // 그룹별 사용자 수
                    "FROM user_analysis " +
                    "GROUP BY analysis_count, age_group " +
                    "ORDER BY " +
                    "  CASE " +  // 분석 횟수 정렬 우선순위
                    "    WHEN analysis_count = 0 THEN 0 " +
                    "    WHEN analysis_count = 1 THEN 1 " +
                    "    WHEN analysis_count = 2 THEN 2 " +
                    "    ELSE 3 " +
                    "  END, " +
                    "  age_group",
            nativeQuery = true
    )
    List<Object[]> findUserCountByAnalysisCountPerAgeGroup();
}