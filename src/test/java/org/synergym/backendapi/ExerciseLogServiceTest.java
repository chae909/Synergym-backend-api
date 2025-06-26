package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.synergym.backendapi.dto.ExerciseLogDTO;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.service.ExerciseLogService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExerciseLogServiceTest {

    @Autowired
    private ExerciseLogService exerciseLogService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoutineRepository routineRepository;

    private User createTestUser() {
        return userRepository.save(
                User.builder()
                        .email("loguser@test.com")
                        .name("운동로그유저")
                        .password("pw123")
                        .goal("체력증진")
                        .build()
        );
    }

    private Routine createTestRoutine(User user) {
        return routineRepository.save(
                Routine.builder()
                        .user(user)
                        .name("테스트루틴")
                        .description("테스트용 루틴")
                        .build()
        );
    }

    @Test
    void saveExerciseLog() {
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineId(routine.getId())
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.85"))
                .memo("저장 테스트")
                .build();
        Integer logId = exerciseLogService.saveExerciseLog(logDTO);
        System.out.println("Saved log ID: " + logId);
        assertNotNull(logId);
    }

    @Test
    void findExerciseLogById() {
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineId(routine.getId())
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.7"))
                .memo("조회 테스트")
                .build();
        Integer logId = exerciseLogService.saveExerciseLog(logDTO);
        ExerciseLogDTO found = exerciseLogService.findExerciseLogById(logId);
        System.out.println("Found log: " + found);
        assertEquals(logDTO.getMemo(), found.getMemo());
    }

    @Test
    void updateExerciseLog() {
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineId(routine.getId())
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.5"))
                .memo("수정 전 메모")
                .build();
        Integer logId = exerciseLogService.saveExerciseLog(logDTO);
        ExerciseLogDTO updateDTO = ExerciseLogDTO.builder()
                .memo("수정된 메모")
                .completionRate(new BigDecimal("0.95"))
                .build();
        exerciseLogService.updateExerciseLog(logId, updateDTO);
        ExerciseLogDTO updated = exerciseLogService.findExerciseLogById(logId);
        System.out.println("Updated log: " + updated);
        assertEquals("수정된 메모", updated.getMemo());
        assertEquals(new BigDecimal("0.95"), updated.getCompletionRate());
    }

    @Test
    void findAllExerciseLogs() {
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineId(routine.getId())
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("1.0"))
                .memo("전체 조회 테스트")
                .build();
        exerciseLogService.saveExerciseLog(logDTO);
        List<ExerciseLogDTO> allLogs = exerciseLogService.findAllExerciseLogs();
        System.out.println("All logs: " + allLogs);
        assertFalse(allLogs.isEmpty());
    }

    @Test
    void deleteExerciseLog() {
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineId(routine.getId())
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.6"))
                .memo("삭제 테스트")
                .build();
        Integer logId = exerciseLogService.saveExerciseLog(logDTO);
        exerciseLogService.deleteExerciseLog(logId);
        System.out.println("Deleted log ID: " + logId);
        assertThrows(RuntimeException.class, () -> exerciseLogService.findExerciseLogById(logId));
    }
} 