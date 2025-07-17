// src/main/java/org/synergym/backendapi/service/StatsServiceImpl.java
package org.synergym.backendapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ComparisonStatsDTO;
import org.synergym.backendapi.repository.ExerciseLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ExerciseLogRepository exerciseLogRepository;

    @Override
    @Transactional(readOnly = true)
    public ComparisonStatsDTO getComparisonStats(Integer userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // 최근 30일간의 기록으로 비교

        // 모든 사용자의 30일간 운동 횟수를 조회
        List<Map<String, Object>> userExerciseCounts = exerciseLogRepository.countLogsGroupByUser(startDate, endDate);

        // 대상 사용자의 운동 횟수 찾기
        long targetUserCount = userExerciseCounts.stream()
                .filter(map -> ((Number) map.get("userId")).longValue() == userId)
                .map(map -> (Long) map.get("count"))
                .findFirst()
                .orElse(0L);

        // 대상 사용자보다 적게 운동한 사용자 수 계산
        long usersWithLessCount = userExerciseCounts.stream()
                .filter(map -> (Long) map.get("count") < targetUserCount)
                .count();

        // 백분위 계산
        double percentile = 0.0;
        if (!userExerciseCounts.isEmpty()) {
            percentile = (double) usersWithLessCount / userExerciseCounts.size() * 100;
        }

        // 코멘트 생성
        String comment;
        if (percentile >= 75) {
            comment = String.format("귀하는 상위 %.0f%%의 꾸준한 사용자입니다! 정말 대단해요!", 100 - percentile);
        } else if (percentile >= 25) {
            comment = "현재 꾸준히 운동 습관을 만들어가고 계시는군요! 잘하고 있어요.";
        } else {
            comment = "이제 막 운동 습관을 만들기 시작하셨네요. Synergym이 함께 응원하겠습니다!";
        }

        return ComparisonStatsDTO.builder()
                .frequencyPercentile(percentile)
                .comment(comment)
                .build();
    }
}