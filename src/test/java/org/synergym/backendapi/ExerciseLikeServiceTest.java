package org.synergym.backendapi;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.repository.ExerciseLikeRepository;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.service.ExerciseLikeService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExerciseLikeServiceTest {

    @Autowired
    private ExerciseLikeService exerciseLikeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseLikeRepository exerciseLikeRepository;

    private static User testUser;
    private static Exercise testExercise;
    private static ExerciseLikeDTO testLikeDTO;

    @BeforeAll
    static void setUp() {
        System.out.println("=== ExerciseLikeService 테스트 시작 ===");
    }

    @BeforeEach
    void createTestData() {
        // 테스트용 사용자 생성
        testUser = userRepository.save(
                User.builder()
                        .email("testuser@test.com")
                        .name("테스트유저")
                        .password("test123")
                        .goal("건강관리")
                        .build()
        );

        // 테스트용 운동 생성
        testExercise = exerciseRepository.save(
                Exercise.builder()
                        .name("스쿼트")
                        .category("하체")
                        .description("하체 근력 운동")
                        .difficulty("초급")
                        .posture("선자세")
                        .bodyPart("하체")
                        .thumbnailUrl("https://example.com/squat.jpg")
                        .build()
        );

        // 테스트용 DTO 생성
        testLikeDTO = ExerciseLikeDTO.builder()
                .userId(testUser.getId())
                .exerciseId(testExercise.getId())
                .build();

        System.out.println("테스트 데이터 생성 완료:");
        System.out.println("- 사용자 ID: " + testUser.getId() + ", 이름: " + testUser.getName());
        System.out.println("- 운동 ID: " + testExercise.getId() + ", 이름: " + testExercise.getName());
    }

    @AfterEach
    void cleanUp() {
        // 테스트 후 데이터 정리
        exerciseLikeRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
        System.out.println("테스트 데이터 정리 완료\n");
    }

    @Test
    @Order(1)
    @DisplayName("좋아요 추가 테스트")
    void add() {
        System.out.println("--- 좋아요 추가 테스트 ---");
        
        // 좋아요 추가 전 상태 확인
        boolean beforeAdd = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("좋아요 추가 전 상태: " + beforeAdd);
        assertFalse(beforeAdd);

        // 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("좋아요 추가 완료");

        // 좋아요 추가 후 상태 확인
        boolean afterAdd = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("좋아요 추가 후 상태: " + afterAdd);
        assertTrue(afterAdd);

        // 사용자별 좋아요 목록 확인
        var userLikes = exerciseLikeService.getByUser(testUser.getId());
        System.out.println("사용자별 좋아요 개수: " + userLikes.size());
        assertEquals(1, userLikes.size());
        assertEquals(testExercise.getId(), userLikes.get(0).getExerciseId());

        // 운동별 좋아요 목록 확인
        var exerciseLikes = exerciseLikeService.getByExercise(testExercise.getId());
        System.out.println("운동별 좋아요 개수: " + exerciseLikes.size());
        assertEquals(1, exerciseLikes.size());
        assertEquals(testUser.getId(), exerciseLikes.get(0).getUserId());
    }

    @Test
    @Order(2)
    @DisplayName("좋아요 삭제 테스트")
    void delete() {
        System.out.println("--- 좋아요 삭제 테스트 ---");
        
        // 먼저 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("좋아요 추가 완료");

        // 삭제 전 상태 확인
        boolean beforeDelete = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("삭제 전 상태: " + beforeDelete);
        assertTrue(beforeDelete);

        // 좋아요 삭제
        exerciseLikeService.delete(testUser.getId(), testExercise.getId());
        System.out.println("좋아요 삭제 완료");

        // 삭제 후 상태 확인
        boolean afterDelete = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("삭제 후 상태: " + afterDelete);
        assertFalse(afterDelete);

        // 사용자별 좋아요 목록 확인
        var userLikes = exerciseLikeService.getByUser(testUser.getId());
        System.out.println("삭제 후 사용자별 좋아요 개수: " + userLikes.size());
        assertTrue(userLikes.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("사용자별 좋아요 조회 테스트")
    void getByUser() {
        System.out.println("--- 사용자별 좋아요 조회 테스트 ---");
        
        // 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("좋아요 추가 완료");

        // 사용자별 좋아요 조회
        var userLikes = exerciseLikeService.getByUser(testUser.getId());
        System.out.println("조회된 좋아요 개수: " + userLikes.size());
        assertFalse(userLikes.isEmpty());
        assertEquals(1, userLikes.size());

        // 좋아요 상세 정보 확인
        var like = userLikes.get(0);
        System.out.println("좋아요 상세 정보:");
        System.out.println("- 사용자 ID: " + like.getUserId());
        System.out.println("- 운동 ID: " + like.getExerciseId());
        assertEquals(testUser.getId(), like.getUserId());
        assertEquals(testExercise.getId(), like.getExerciseId());
    }

    @Test
    @Order(4)
    @DisplayName("운동별 좋아요 조회 테스트")
    void getByExercise() {
        System.out.println("--- 운동별 좋아요 조회 테스트 ---");
        
        // 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("좋아요 추가 완료");

        // 운동별 좋아요 조회
        var exerciseLikes = exerciseLikeService.getByExercise(testExercise.getId());
        System.out.println("조회된 좋아요 개수: " + exerciseLikes.size());
        assertFalse(exerciseLikes.isEmpty());
        assertEquals(1, exerciseLikes.size());

        // 좋아요 상세 정보 확인
        var like = exerciseLikes.get(0);
        System.out.println("좋아요 상세 정보:");
        System.out.println("- 사용자 ID: " + like.getUserId());
        System.out.println("- 운동 ID: " + like.getExerciseId());
        assertEquals(testUser.getId(), like.getUserId());
        assertEquals(testExercise.getId(), like.getExerciseId());
    }

    @Test
    @Order(5)
    @DisplayName("좋아요 상태 확인 테스트")
    void isLiked() {
        System.out.println("--- 좋아요 상태 확인 테스트 ---");
        
        // 좋아요 추가 전 상태 확인
        boolean beforeAdd = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("좋아요 추가 전 상태: " + beforeAdd);
        assertFalse(beforeAdd);

        // 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("좋아요 추가 완료");

        // 좋아요 추가 후 상태 확인
        boolean afterAdd = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("좋아요 추가 후 상태: " + afterAdd);
        assertTrue(afterAdd);

        // 존재하지 않는 사용자/운동 조합 확인 (예외 발생 예상)
        assertThrows(EntityNotFoundException.class, () -> {
            exerciseLikeService.isLiked(999, 999);
        }, "존재하지 않는 사용자/운동에 대해서는 예외가 발생해야 합니다");
        System.out.println("존재하지 않는 사용자/운동에 대한 예외 발생 확인");
    }

    @Test
    @Order(6)
    @DisplayName("중복 좋아요 방지 테스트")
    void duplicateLikePrevention() {
        System.out.println("--- 중복 좋아요 방지 테스트 ---");
        
        // 첫 번째 좋아요 추가
        exerciseLikeService.add(testLikeDTO);
        System.out.println("첫 번째 좋아요 추가 완료");

        // 좋아요 개수 확인
        var userLikesBefore = exerciseLikeService.getByUser(testUser.getId());
        System.out.println("첫 번째 추가 후 좋아요 개수: " + userLikesBefore.size());
        assertEquals(1, userLikesBefore.size());

        // 중복 좋아요 시도 (예외 발생 예상)
        assertThrows(IllegalStateException.class, () -> {
            exerciseLikeService.add(testLikeDTO);
        }, "중복 좋아요 시도 시 예외가 발생해야 합니다");
        System.out.println("중복 좋아요 시도 시 예외 발생 확인");

        // 좋아요 개수 확인 (여전히 1개여야 함)
        var userLikesAfter = exerciseLikeService.getByUser(testUser.getId());
        System.out.println("중복 시도 후 좋아요 개수: " + userLikesAfter.size());
        assertEquals(1, userLikesAfter.size());

        // 좋아요 상태 확인
        boolean isLiked = exerciseLikeService.isLiked(testUser.getId(), testExercise.getId());
        System.out.println("중복 시도 후 좋아요 상태: " + isLiked);
        assertTrue(isLiked);
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== ExerciseLikeService 테스트 완료 ===");
    }
}
