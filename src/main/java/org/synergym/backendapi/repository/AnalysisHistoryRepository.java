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
            "  END, " + // <-- AS ageGroup 별칭은 SELECT 결과에서만 유효합니다.
            "  AVG((ah.spineCurvScore + ah.spineScolScore + ah.pelvicScore + ah.neckScore + ah.shoulderScore) / 5.0) " +
            "FROM AnalysisHistory ah JOIN ah.user u " +
            "WHERE u.birthday IS NOT NULL " +
            // ▼▼▼ GROUP BY와 ORDER BY에서 별칭 대신 CASE 표현식 전체를 사용 ▼▼▼
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
}
