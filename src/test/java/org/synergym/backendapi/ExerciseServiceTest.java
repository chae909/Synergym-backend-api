package org.synergym.backendapi;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.service.ExerciseService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExerciseServiceTest {

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private static ExerciseDTO testExerciseDTO;

    @BeforeAll
    static void setUp() {
        System.out.println("=== ExerciseService 테스트 시작 ===");
    }

    @BeforeEach
    void createTestData() {
        // 테스트용 운동 DTO 생성
        testExerciseDTO = ExerciseDTO.builder()
                .name("스쿼트")
                .category("하체")
                .description("하체 근력 운동")
                .difficulty("초급")
                .posture("선자세")
                .bodyPart("하체")
                .thumbnailUrl("https://example.com/squat.jpg")
                .build();

        System.out.println("테스트 데이터 생성 완료:");
        System.out.println("- 운동 이름: " + testExerciseDTO.getName());
        System.out.println("- 운동 카테고리: " + testExerciseDTO.getCategory());
    }

    @AfterEach
    void cleanUp() {
        // 테스트 후 데이터 정리
        exerciseRepository.deleteAll();
        System.out.println("테스트 데이터 정리 완료\n");
    }

    @Test
    @Order(1)
    @DisplayName("운동 생성 테스트")
    void createExercise() {
        System.out.println("--- 운동 생성 테스트 ---");
        
        // 운동 생성
        Integer exerciseId = exerciseService.createExercise(testExerciseDTO);
        System.out.println("생성된 운동 ID: " + exerciseId);
        
        assertNotNull(exerciseId);
        assertTrue(exerciseId > 0);

        // 생성된 운동 조회하여 확인
        ExerciseDTO createdExercise = exerciseService.getExerciseById(exerciseId);
        System.out.println("생성된 운동 정보: " + createdExercise.getName());
        
        assertEquals(testExerciseDTO.getName(), createdExercise.getName());
        assertEquals(testExerciseDTO.getCategory(), createdExercise.getCategory());
        assertEquals(testExerciseDTO.getDescription(), createdExercise.getDescription());
    }

    @Test
    @Order(2)
    @DisplayName("운동 ID 조회 테스트")
    void getExerciseById() {
        System.out.println("--- 운동 ID 조회 테스트 ---");
        
        // 먼저 운동 생성
        Integer exerciseId = exerciseService.createExercise(testExerciseDTO);
        System.out.println("생성된 운동 ID: " + exerciseId);

        // ID로 운동 조회
        ExerciseDTO foundExercise = exerciseService.getExerciseById(exerciseId);
        System.out.println("조회된 운동: " + foundExercise.getName());
        
        assertNotNull(foundExercise);
        assertEquals(exerciseId, foundExercise.getId());
        assertEquals(testExerciseDTO.getName(), foundExercise.getName());
        assertEquals(testExerciseDTO.getCategory(), foundExercise.getCategory());
    }

    @Test
    @Order(3)
    @DisplayName("운동 ID 조회 실패 테스트")
    void getExerciseById_NotFound() {
        System.out.println("--- 운동 ID 조회 실패 테스트 ---");
        
        // 존재하지 않는 ID로 조회 시도
        assertThrows(EntityNotFoundException.class, () -> {
            exerciseService.getExerciseById(999999);
        }, "존재하지 않는 운동 ID로 조회 시 예외가 발생해야 합니다");
        System.out.println("존재하지 않는 운동 ID 조회 시 예외 발생 확인");
    }

    @Test
    @Order(4)
    @DisplayName("전체 운동 조회 테스트")
    void getAllExercises() {
        System.out.println("--- 전체 운동 조회 테스트 ---");
        
        // 여러 운동 생성
        ExerciseDTO exercise1 = ExerciseDTO.builder()
                .name("푸시업")
                .category("상체")
                .description("가슴 근육 운동")
                .difficulty("초급")
                .posture("엎드린자세")
                .bodyPart("가슴")
                .thumbnailUrl("https://example.com/pushup.jpg")
                .build();

        ExerciseDTO exercise2 = ExerciseDTO.builder()
                .name("플랭크")
                .category("코어")
                .description("코어 근육 운동")
                .difficulty("중급")
                .posture("엎드린자세")
                .bodyPart("복부")
                .thumbnailUrl("https://example.com/plank.jpg")
                .build();

        exerciseService.createExercise(exercise1);
        exerciseService.createExercise(exercise2);
        System.out.println("테스트용 운동 2개 생성 완료");

        // 전체 운동 조회
        var exercises = exerciseService.getAllExercises();
        System.out.println("전체 운동 개수: " + exercises.size());
        
        assertNotNull(exercises);
        assertFalse(exercises.isEmpty());
        assertTrue(exercises.size() >= 2);
        
        // 운동 목록 출력
        exercises.forEach(exercise -> 
            System.out.println("- " + exercise.getName() + " (" + exercise.getCategory() + ")")
        );
    }

    @Test
    @Order(5)
    @DisplayName("운동 이름 검색 테스트")
    void getExercisesByName() {
        System.out.println("--- 운동 이름 검색 테스트 ---");
        
        // 테스트용 운동 생성
        exerciseService.createExercise(testExerciseDTO);
        
        ExerciseDTO exercise2 = ExerciseDTO.builder()
                .name("스쿼트 점프")
                .category("하체")
                .description("폭발력 운동")
                .difficulty("중급")
                .posture("선자세")
                .bodyPart("하체")
                .thumbnailUrl("https://example.com/squat-jump.jpg")
                .build();
        exerciseService.createExercise(exercise2);
        System.out.println("테스트용 운동 2개 생성 완료");

        // "스쿼트" 포함된 운동 검색
        var squats = exerciseService.getExercisesByName("스쿼트");
        System.out.println("'스쿼트' 포함 운동 개수: " + squats.size());
        
        assertNotNull(squats);
        assertTrue(squats.size() >= 2);
        
        // 검색 결과 출력
        squats.forEach(exercise -> 
            System.out.println("- " + exercise.getName())
        );
        
        // 모든 운동이 "스쿼트"를 포함하는지 확인
        squats.forEach(exercise -> 
            assertTrue(exercise.getName().contains("스쿼트"))
        );
    }

    @Test
    @Order(6)
    @DisplayName("운동 카테고리별 조회 테스트")
    void getExercisesByCategory() {
        System.out.println("--- 운동 카테고리별 조회 테스트 ---");
        
        // 하체 운동 생성
        exerciseService.createExercise(testExerciseDTO);
        
        ExerciseDTO upperBody = ExerciseDTO.builder()
                .name("푸시업")
                .category("상체")
                .description("가슴 근육 운동")
                .difficulty("초급")
                .posture("엎드린자세")
                .bodyPart("가슴")
                .thumbnailUrl("https://example.com/pushup.jpg")
                .build();
        exerciseService.createExercise(upperBody);
        System.out.println("하체, 상체 운동 생성 완료");

        // 하체 운동만 조회
        var lowerBodyExercises = exerciseService.getExercisesByCategory("하체");
        System.out.println("하체 운동 개수: " + lowerBodyExercises.size());
        
        assertNotNull(lowerBodyExercises);
        assertTrue(lowerBodyExercises.size() >= 1);
        
        // 하체 운동 목록 출력
        lowerBodyExercises.forEach(exercise -> {
            System.out.println("- " + exercise.getName() + " (" + exercise.getCategory() + ")");
            assertEquals("하체", exercise.getCategory());
        });
    }

    @Test
    @Order(7)
    @DisplayName("운동 삭제 테스트")
    void deleteExercise() {
        System.out.println("--- 운동 삭제 테스트 ---");
        
        // 운동 생성
        Integer exerciseId = exerciseService.createExercise(testExerciseDTO);
        System.out.println("생성된 운동 ID: " + exerciseId);

        // 삭제 전 존재 확인
        ExerciseDTO beforeDelete = exerciseService.getExerciseById(exerciseId);
        assertNotNull(beforeDelete);
        System.out.println("삭제 전 운동 존재 확인: " + beforeDelete.getName());

        // 운동 삭제
        exerciseService.deleteExercise(exerciseId);
        System.out.println("운동 삭제 완료");

        // 삭제 후 존재하지 않음 확인
        assertThrows(EntityNotFoundException.class, () -> {
            exerciseService.getExerciseById(exerciseId);
        }, "삭제된 운동을 조회할 수 없어야 합니다");
        System.out.println("삭제된 운동 조회 시 예외 발생 확인");
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== ExerciseService 테스트 완료 ===");
    }
}
