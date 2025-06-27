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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
                        .email("loguser_" + UUID.randomUUID() + "@test.com")
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
    void createExerciseLog() {
        System.out.println("\n========== [createExerciseLog] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.85"))
                .memo("저장 테스트")
                .build();
        Integer logId = exerciseLogService.createExerciseLog(logDTO);
        System.out.printf("생성된 logId: %d, userId: %d, routineIds: %s, memo: %s\n",
                logId, user.getId(), logDTO.getRoutineIds(), logDTO.getMemo());
        assertNotNull(logId);
        System.out.println("========== [createExerciseLog] 테스트 끝 ==========");
    }

    @Test
    void getExerciseLogById() {
        System.out.println("\n========== [getExerciseLogById] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.7"))
                .memo("조회 테스트")
                .build();
        Integer logId = exerciseLogService.createExerciseLog(logDTO);
        ExerciseLogDTO found = exerciseLogService.getExerciseLogById(logId);
        System.out.printf("조회된 logId: %d, userId: %d, routineIds: %s, memo: %s\n",
                found.getId(), found.getUserId(), found.getRoutineIds(), found.getMemo());
        assertEquals(logDTO.getMemo(), found.getMemo());
        assertEquals(user.getId(), found.getUserId());
        assertTrue(found.getRoutineIds().contains(routine.getId()));
        System.out.println("========== [getExerciseLogById] 테스트 끝 ==========");
    }

    @Test
    void getAllExerciseLogs() {
        System.out.println("\n========== [getAllExerciseLogs] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("1.0"))
                .memo("전체 조회 테스트")
                .build();
        exerciseLogService.createExerciseLog(logDTO);
        List<ExerciseLogDTO> allLogs = exerciseLogService.getAllExerciseLogs();
        System.out.printf("전체 로그 개수: %d\n", allLogs.size());
        for (ExerciseLogDTO log : allLogs) {
            System.out.printf("  - logId: %d, userId: %d, routineIds: %s, memo: %s\n",
                    log.getId(), log.getUserId(), log.getRoutineIds(), log.getMemo());
        }
        assertFalse(allLogs.isEmpty());
        System.out.println("========== [getAllExerciseLogs] 테스트 끝 ==========");
    }

    @Test
    void getExerciseLogsByUser() {
        System.out.println("\n========== [getExerciseLogsByUser] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.9"))
                .memo("사용자별 조회 테스트")
                .build();
        exerciseLogService.createExerciseLog(logDTO);
        List<ExerciseLogDTO> userLogs = exerciseLogService.getExerciseLogsByUser(user.getId());
        System.out.printf("userId %d의 로그 개수: %d\n", user.getId(), userLogs.size());
        for (ExerciseLogDTO log : userLogs) {
            System.out.printf("  - logId: %d, routineIds: %s, memo: %s\n",
                    log.getId(), log.getRoutineIds(), log.getMemo());
        }
        assertTrue(userLogs.stream().anyMatch(log -> log.getUserId().equals(user.getId())));
        System.out.println("========== [getExerciseLogsByUser] 테스트 끝 ==========");
    }

    @Test
    void getExerciseLogsByUserAndDate() {
        System.out.println("\n========== [getExerciseLogsByUserAndDate] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        LocalDate today = LocalDate.now();
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(today)
                .completionRate(new BigDecimal("0.8"))
                .memo("사용자+날짜별 조회 테스트")
                .build();
        exerciseLogService.createExerciseLog(logDTO);
        List<ExerciseLogDTO> logs = exerciseLogService.getExerciseLogsByUserAndDate(user.getId(), today);
        System.out.printf("userId %d, 날짜 %s의 로그 개수: %d\n", user.getId(), today, logs.size());
        for (ExerciseLogDTO log : logs) {
            System.out.printf("  - logId: %d, routineIds: %s, memo: %s\n",
                    log.getId(), log.getRoutineIds(), log.getMemo());
        }
        assertTrue(logs.stream().allMatch(log -> log.getUserId().equals(user.getId()) && today.equals(log.getExerciseDate())));
        System.out.println("========== [getExerciseLogsByUserAndDate] 테스트 끝 ==========");
    }

    @Test
    void deleteExerciseLog() {
        System.out.println("\n========== [deleteExerciseLog] 테스트 시작 ==========");
        User user = createTestUser();
        Routine routine = createTestRoutine(user);
        ExerciseLogDTO logDTO = ExerciseLogDTO.builder()
                .userId(user.getId())
                .routineIds(Collections.singletonList(routine.getId()))
                .exerciseDate(LocalDate.now())
                .completionRate(new BigDecimal("0.6"))
                .memo("삭제 테스트")
                .build();
        Integer logId = exerciseLogService.createExerciseLog(logDTO);
        exerciseLogService.deleteExerciseLog(logId);
        System.out.printf("삭제된 logId: %d\n", logId);
        assertThrows(Exception.class, () -> exerciseLogService.getExerciseLogById(logId));
        System.out.println("========== [deleteExerciseLog] 테스트 끝 ==========");
    }
} 