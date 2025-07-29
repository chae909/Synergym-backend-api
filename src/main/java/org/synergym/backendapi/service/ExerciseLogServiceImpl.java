package org.synergym.backendapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.EmotionType;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.ExerciseLogRoutine;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.ExerciseLogRepository;
import org.synergym.backendapi.repository.ExerciseLogRoutineRepository;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseLogServiceImpl implements ExerciseLogService {
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseLogRoutineRepository exerciseLogRoutineRepository;
    private final EmotionLogService emotionLogService;
    private final WebClient fastApiWebClient;

    //ID로 사용자 조회 (없으면 예외 발생)
    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    //ID로 운동 기록 조회 (없으면 예외 발생)
    private ExerciseLog findExerciseLogById(int id) {
        return exerciseLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_LOG_NOT_FOUND));
    }

    //운동 기록 생성 - 여러 루틴 연동 가능
    @Override
    @Transactional
    public Integer createExerciseLog(ExerciseLogDTO dto) {
        User user = findUserById(dto.getUserId());
        ExerciseLog log = DTOtoEntity(dto);
        log.updateUser(user);
        log = exerciseLogRepository.save(log);

        // 여러 Routine 연동
        if (dto.getRoutineIds() != null) {
            for (Integer routineId : dto.getRoutineIds()) {
                Routine routine = routineRepository.findById(routineId)
                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_NOT_FOUND));
                ExerciseLogRoutine logRoutine = ExerciseLogRoutine.builder()
                        .exerciseLog(log)
                        .routine(routine)
                        .build();

                if (log.getCompletionRate() != null && log.getCompletionRate().compareTo(new BigDecimal("100.00")) == 0) {
                    logRoutine.updateCheckYn('Y');
                }

                exerciseLogRoutineRepository.save(logRoutine);
            }
        }
        
        // call classification(FastAPI)
        EmotionResponseDTO response = fastApiWebClient.post()
            .uri("/emotion")
            .bodyValue(Map.of("memo", dto.getMemo()))
            .retrieve()
            .bodyToMono(EmotionResponseDTO.class)
            .block();

        String emotion = response.getLabel();

        EmotionType emotionType = EmotionType.valueOf(emotion); // 이미 Enum에 맞게 매핑되어 있으니 바로 변환

        EmotionLogDTO emotionLogDTO = EmotionLogDTO.builder()
            .userId(dto.getUserId())
            .exerciseDate(dto.getExerciseDate())
            .emotion(emotionType)
            .memo(dto.getMemo())
            .build();

        emotionLogService.saveOrUpdateEmotionLog(emotionLogDTO);

        return log.getId();
    }

    @Override
    @Transactional
    public void updateExerciseLog(Integer id, ExerciseLogDTO dto) {
        ExerciseLog log = findExerciseLogById(id);

        // 달성률 업데이트
        if (dto.getCompletionRate() != null) {
            log.updateCompletionRate(dto.getCompletionRate());
        }

        // 메모 업데이트
        log.updateMemo(dto.getMemo());

        // 감정 분석
        try {
            
            // FastAPI로 감정 분석 요청 보내기
            EmotionResponseDTO response = fastApiWebClient.post()
                .uri("/emotion")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new EmotionAnalysisRequest(dto.getMemo()))
                .retrieve()
                .bodyToMono(EmotionResponseDTO.class)
                .block();

            if (response != null && response.getLabel() != null) {
                
                EmotionLogDTO emotionLogDTO = EmotionLogDTO.builder()
                    .userId(log.getUser().getId())
                    .exerciseDate(log.getExerciseDate())
                    .emotion(EmotionType.valueOf(response.getLabel()))
                    .memo(dto.getMemo())
                    .build();

                // 기존 감정 로그가 있다면 ID 설정
                if (log.getEmotionLog() != null) {
                    emotionLogDTO.setId(log.getEmotionLog().getId());
                }

                emotionLogService.saveOrUpdateEmotionLog(emotionLogDTO);
            } else {
            }
        } catch (Exception e) {

        }

        // 연관된 ExerciseLogRoutine의 checkYn 업데이트
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        if (!logRoutines.isEmpty()) {
            ExerciseLogRoutine logRoutine = logRoutines.get(0);
            if (log.getCompletionRate() != null && log.getCompletionRate().compareTo(new BigDecimal("100.00")) == 0) {
                logRoutine.updateCheckYn('Y');
            } else {
                logRoutine.updateCheckYn('N');
            }
        }
    }


//전체 운동 기록 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLogDTO> getAllExerciseLogs() {
        return exerciseLogRepository.findAll().stream()
                .map(log -> {
                    List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
                    return entityToDTO(log, logRoutines);
                })
                .collect(Collectors.toList());
    }

    //ID로 운동 기록 단건 조회
    @Override
    @Transactional(readOnly = true)
    public ExerciseLogDTO getExerciseLogById(Integer id) {
        ExerciseLog log = findExerciseLogById(id);
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        return entityToDTO(log, logRoutines);
    }

    //사용자별 전체 운동 기록 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLogDTO> getExerciseLogsByUser(Integer userId) {
        List<ExerciseLog> logs = exerciseLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null && Integer.valueOf(log.getUser().getId()).equals(userId))
                .collect(Collectors.toList());
        List<ExerciseLogDTO> result = new ArrayList<>();
        for (ExerciseLog log : logs) {
            List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
            result.add(entityToDTO(log, logRoutines));
        }
        return result;
    }

    //사용자별 특정 날짜의 운동 기록 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLogDTO> getExerciseLogsByUserAndDate(Integer userId, LocalDate date) {
        List<ExerciseLog> logs = exerciseLogRepository.findByExerciseDate(date).stream()
                .filter(log -> log.getUser() != null && Integer.valueOf(log.getUser().getId()).equals(userId))
                .collect(Collectors.toList());
        List<ExerciseLogDTO> result = new ArrayList<>();
        for (ExerciseLog log : logs) {
            List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
            result.add(entityToDTO(log, logRoutines));
        }
        return result;
    }

    //운동 기록 삭제 - 연관된 ExerciseLogRoutine도 함께 삭제
    @Override
    @Transactional
    public void deleteExerciseLog(Integer id) {
        ExerciseLog log = findExerciseLogById(id);
        // 연관된 ExerciseLogRoutine도 삭제
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        exerciseLogRoutineRepository.deleteAll(logRoutines);
        exerciseLogRepository.delete(log);
    }

    //주간/월간 운동 기록 통계 조회
    @Override
    public WeeklyMonthlyStats getStats(Integer userId, LocalDate weekStart, LocalDate weekEnd, 
                                      LocalDate monthStart, LocalDate monthEnd) {
        // 주간 통계 조회
        Integer weeklyCount = exerciseLogRepository.countByUserIdAndDateBetween(userId, weekStart, weekEnd);

        // 월간 통계 조회
        Integer monthlyCount = exerciseLogRepository.countByUserIdAndDateBetween(userId, monthStart, monthEnd);

        // null 값 처리
        weeklyCount = weeklyCount != null ? weeklyCount : 0;
        
        monthlyCount = monthlyCount != null ? monthlyCount : 0;

        return new WeeklyMonthlyStats(
            weeklyCount,
            monthlyCount
        );
    }
    
    //사용자별 이번 주 운동 기록 통계 조회
    @Override
    @Transactional(readOnly = true)
    public WeeklyMonthlyStats getWeeklyStats(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        
        // 주간 통계만 조회
        Integer weeklyCount = exerciseLogRepository.countByUserIdAndDateBetween(userId, weekStart, weekEnd);
        
        // null 값 처리
        weeklyCount = weeklyCount != null ? weeklyCount : 0;
        
        return new WeeklyMonthlyStats(
            weeklyCount, // 주간 데이터
            0
        );
    }
    
    //사용자별 이번 달 운동 기록 통계 조회
    @Override
    @Transactional(readOnly = true)
    public WeeklyMonthlyStats getMonthlyStats(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());
        
        // 월간 통계만 조회
        Integer monthlyCount = exerciseLogRepository.countByUserIdAndDateBetween(userId, monthStart, monthEnd);
        
        // null 값 처리
        monthlyCount = monthlyCount != null ? monthlyCount : 0;
        
        return new WeeklyMonthlyStats(
            0,
            monthlyCount
        );
    }
}