package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.entity.ExerciseLog;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.ExerciseLogRepository;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseLogServiceImpl implements ExerciseLogService {
    private final ExerciseLogRepository exerciseLogRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    @Transactional
    @Override
    public Integer saveExerciseLog(ExerciseLogDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Routine routine = routineRepository.findById(dto.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Routine not found"));
        ExerciseLog log = dtoToEntity(dto);
        // user, routine은 ServiceImpl에서 주입
        log = ExerciseLog.builder()
                .user(user)
                .routine(routine)
                .exerciseDate(dto.getExerciseDate())
                .completionRate(dto.getCompletionRate())
                .memo(dto.getMemo())
                .build();
        log = exerciseLogRepository.save(log);
        return log.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExerciseLogDTO> findAllExerciseLogs() {
        return exerciseLogRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ExerciseLogDTO findExerciseLogById(Integer id) {
        ExerciseLog log = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExerciseLog not found"));
        return entityToDto(log);
    }

    @Transactional
    @Override
    public void deleteExerciseLog(Integer id) {
        exerciseLogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateExerciseLog(Integer id, ExerciseLogDTO dto) {
        ExerciseLog log = exerciseLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExerciseLog not found"));
        // user, routine 변경이 요청된 경우 처리
        if (dto.getUserId() != null && (log.getUser() == null || !Integer.valueOf(log.getUser().getId()).equals(dto.getUserId()))) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.updateUser(user);
        }
        if (dto.getRoutineId() != null && (log.getRoutine() == null || !Integer.valueOf(log.getRoutine().getId()).equals(dto.getRoutineId()))) {
            Routine routine = routineRepository.findById(dto.getRoutineId())
                    .orElseThrow(() -> new RuntimeException("Routine not found"));
            log.updateRoutine(routine);
        }
        // 나머지 필드 업데이트
        updateEntityFromDto(log, dto);
        exerciseLogRepository.save(log);
    }
} 