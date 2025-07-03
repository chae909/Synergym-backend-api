package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.entity.ExerciseLogRoutine;

import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.ExerciseLogRepository;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.repository.ExerciseLogRoutineRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ExerciseLogServiceImpl implements ExerciseLogService {
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseLogRoutineRepository exerciseLogRoutineRepository;

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private ExerciseLog findExerciseLogById(int id) {
        return exerciseLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_LOG_NOT_FOUND));
    }

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

        // 연관된 ExerciseLogRoutine의 checkYn 업데이트
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        if (!logRoutines.isEmpty()) {
            ExerciseLogRoutine logRoutine = logRoutines.get(0); // 하나의 로그는 하나의 루틴만 가짐
            if (log.getCompletionRate() != null && log.getCompletionRate().compareTo(new BigDecimal("100.00")) == 0) {
                logRoutine.updateCheckYn('Y');
            } else {
                logRoutine.updateCheckYn('N');
            }
        }
    }

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

    @Override
    @Transactional(readOnly = true)
    public ExerciseLogDTO getExerciseLogById(Integer id) {
        ExerciseLog log = findExerciseLogById(id);
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        return entityToDTO(log, logRoutines);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLogDTO> getExerciseLogsByUser(Integer userId) {
        User user = findUserById(userId);
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

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLogDTO> getExerciseLogsByUserAndDate(Integer userId, LocalDate date) {
        User user = findUserById(userId);
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

    @Override
    @Transactional
    public void deleteExerciseLog(Integer id) {
        ExerciseLog log = findExerciseLogById(id);
        // 연관된 ExerciseLogRoutine도 삭제
        List<ExerciseLogRoutine> logRoutines = exerciseLogRoutineRepository.findByExerciseLog(log);
        exerciseLogRoutineRepository.deleteAll(logRoutines);
        exerciseLogRepository.delete(log);
    }
} 