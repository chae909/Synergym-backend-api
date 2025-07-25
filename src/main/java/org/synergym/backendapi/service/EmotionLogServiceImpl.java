package org.synergym.backendapi.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.EmotionAnalysisRequest;
import org.synergym.backendapi.dto.EmotionLogDTO;
import org.synergym.backendapi.dto.EmotionResponseDTO;
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
    private final WebClient fastApiWebClient;

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public EmotionLogDTO saveOrUpdateEmotionLog(EmotionLogDTO dto) {
        // --- 시작: 메모가 비어있을 때의 처리
        User user = findUserById(dto.getUserId());
        if (dto.getMemo() == null || dto.getMemo().isBlank()) {
            if (dto.getId() != 0) {
                emotionLogRepository.findById(dto.getId()).ifPresent(emotionLog -> {
                    if(emotionLog.getExerciseLog() != null) {
                        emotionLog.getExerciseLog().setEmotionLog(null);
                    }
                    emotionLogRepository.delete(emotionLog);
                });
            }
            return null;
        }
        // --- 끝: 메모가 비어있을 때의 처리

        // --- 시작: FastAPI 호출
        EmotionResponseDTO emotionResponse = fastApiWebClient.post()
                .uri("/emotion")
                .bodyValue(new EmotionAnalysisRequest(dto.getMemo()))
                .retrieve()
                .bodyToMono(EmotionResponseDTO.class)
                .block();

        if (emotionResponse == null || emotionResponse.getLabel() == null) {
            throw new IllegalStateException("메모로부터 감정을 분류할 수 없습니다.");
        }
        // --- 끝: FastAPI 호출 ---


        // --- 시작: '메인' 운동 로그 찾기
        List<ExerciseLog> logsForDay = exerciseLogRepository.findByUserAndExerciseDate(user, dto.getExerciseDate());

        Optional<ExerciseLog> mainLogOptional = logsForDay.stream()
                .filter(log -> log.getEmotionLog() != null)
                .findFirst();

        ExerciseLog exerciseLog;
        if (mainLogOptional.isPresent()) {
            exerciseLog = mainLogOptional.get();
        } else {
            exerciseLog = logsForDay.stream().findFirst().orElse(null);
        }
        // --- 끝: '메인' 운동 로그 찾기

        // --- 시작: 운동 로그 생성 또는 업데이트
        if (exerciseLog == null) {
            ExerciseLog newExerciseLog = ExerciseLog.builder()
                    .user(user)
                    .exerciseDate(dto.getExerciseDate())
                    .completionRate(BigDecimal.ZERO)
                    .memo(dto.getMemo())
                    .build();
            exerciseLog = exerciseLogRepository.save(newExerciseLog);
        } else {
            exerciseLog.updateMemo(dto.getMemo());
        }
        // --- 끝: 운동 로그 생성 또는 업데이트 ---

        EmotionType classifiedEmotion = EmotionType.valueOf(emotionResponse.getLabel());

        Optional<EmotionLog> emotionLogOptional = emotionLogRepository.findByExerciseLog(exerciseLog);

        EmotionLog emotionLog;
        if (emotionLogOptional.isPresent()) {
            emotionLog = emotionLogOptional.get();
        } else {
            emotionLog = EmotionLog.builder()
                    .exerciseLog(exerciseLog)
                    .build();
        }

        emotionLog.updateEmotion(classifiedEmotion);
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
        EmotionLog log = emotionLogRepository.findById(emotionLogId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EMOTION_LOG_NOT_FOUND));

        log.getExerciseLog().setEmotionLog(null);
        emotionLogRepository.delete(log);
    }

    @Override
    @Transactional(readOnly = true)
    public EmotionStatsDTO getEmotionStats(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        Map<EmotionType, Long> weeklyStats = getStatsForPeriod(userId, startOfWeek, endOfWeek); //
        Map<EmotionType, Long> monthlyStats = getStatsForPeriod(userId, startOfMonth, endOfMonth); //

        return new EmotionStatsDTO(weeklyStats, monthlyStats); //
    }

    private Map<EmotionType, Long> getStatsForPeriod(Integer userId, LocalDate startDate, LocalDate endDate) {
        return emotionLogRepository.countEmotionsByDateRange(userId, startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (EmotionType) obj[0],
                        obj -> (Long) obj[1]
                ));
    }
}