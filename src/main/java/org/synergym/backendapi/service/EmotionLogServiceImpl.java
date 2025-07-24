package org.synergym.backendapi.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.EmotionLogDTO;
import org.synergym.backendapi.dto.EmotionStatsDTO;
import org.synergym.backendapi.entity.EmotionLog;
import org.synergym.backendapi.entity.EmotionType;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.EmotionLogRepository;
import org.synergym.backendapi.repository.ExerciseLogRepository;
import org.synergym.backendapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmotionLogServiceImpl implements EmotionLogService {
    private final EmotionLogRepository emotionLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public EmotionLogDTO saveOrUpdateEmotionLog(EmotionLogDTO dto) {
        User user = findUserById(dto.getUserId());
        // 1. 해당 날짜의 ExerciseLog를 찾거나 새로 생성합니다.
        ExerciseLog foundExerciseLog = exerciseLogRepository.findByUserAndExerciseDate(user, dto.getExerciseDate())
                .orElse(null);

        ExerciseLog exerciseLog;
        if (foundExerciseLog == null) {
            ExerciseLog newExerciseLog = ExerciseLog.builder()
                    .user(user)
                    .exerciseDate(dto.getExerciseDate())
                    .completionRate(BigDecimal.ZERO) // 감성 기록만 있을 경우 0%
                    .memo(dto.getMemo())
                    .build();
            exerciseLog = exerciseLogRepository.save(newExerciseLog);
        } else {
            // 만약 ExerciseLog가 이미 존재했다면, memo를 업데이트합니다.
            foundExerciseLog.updateMemo(dto.getMemo());
            exerciseLog = foundExerciseLog;
        }

        // 2. ExerciseLog에 연결된 EmotionLog를 찾거나 새로 생성합니다.
        EmotionLog emotionLog = emotionLogRepository.findByExerciseLog(exerciseLog)
                .orElseGet(() -> EmotionLog.builder()
                        .exerciseLog(exerciseLog)
                        .emotion(dto.getEmotion())
                        .build());
        
        // 감정을 업데이트합니다.
        emotionLog.updateEmotion(dto.getEmotion());

        EmotionLog savedEmotionLog = emotionLogRepository.save(emotionLog);
        
        return entityToDTO(savedEmotionLog);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<EmotionLogDTO> getEmotionLogsByUser(Integer userId) {
        List<EmotionLog> logs = emotionLogRepository.findByUserWithExerciseLog(userId);
        return logs.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmotionLog(Integer emotionLogId) {
        // EmotionLog를 삭제하면 ExerciseLog의 연관관계(orphanRemoval=true)에 의해 함께 삭제됩니다.
        // 만약 ExerciseLog를 남기고 싶다면, EmotionLog만 찾아서 삭제해야 합니다.
        EmotionLog log = emotionLogRepository.findById(emotionLogId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND));
        
        // ExerciseLog는 남기고 EmotionLog만 삭제
        log.getExerciseLog().setEmotionLog(null);
        emotionLogRepository.delete(log);
    }

    // 감정 통계 조회 로직
    @Override
    @Transactional(readOnly = true)
    public EmotionStatsDTO getEmotionStats(Integer userId) {
        LocalDate today = LocalDate.now();

        // 이번 주 월요일 ~ 일요일
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 이번 달 1일 ~ 마지막 날
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        // 주간, 월간 통계를 DB에서 직접 계산
        Map<EmotionType, Long> weeklyStats = getStatsForPeriod(userId, startOfWeek, endOfWeek);
        Map<EmotionType, Long> monthlyStats = getStatsForPeriod(userId, startOfMonth, endOfMonth);

        return new EmotionStatsDTO(weeklyStats, monthlyStats);
    }

    // 기간별 통계를 계산하는 private 헬퍼 메소드
    private Map<EmotionType, Long> getStatsForPeriod(Integer userId, LocalDate startDate, LocalDate endDate) {
        return emotionLogRepository.countEmotionsByDateRange(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                    obj -> (EmotionType) obj[0],
                    obj -> (Long) obj[1]
                ));
    }
}