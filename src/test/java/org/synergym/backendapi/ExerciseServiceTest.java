package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.service.ExerciseService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExerciseServiceTest {

    @Autowired
    private ExerciseService exerciseService;


    @Test
    void createExercise() {
        ExerciseDTO newExercise = ExerciseDTO.builder()
                .name("스쿼트")
                .category("근력운동")
                .description("하체 근육을 강화하는 운동")
                .difficulty("초급")
                .posture("선자세")
                .bodyPart("하체")
                .thumbnailUrl("https://example.com/squat.jpg")
                .build();

        Integer exerciseId = exerciseService.saveExercise(newExercise);
        assertNotNull(exerciseId);
        assertTrue(exerciseId > 0);
        System.out.println("생성된 운동 ID: " + exerciseId);
    }

    @Test
    void findAllExercises() {
        ExerciseDTO newExercise = ExerciseDTO.builder()
                .name("푸시업")
                .category("근력운동")
                .description("가슴 근육을 강화하는 운동")
                .difficulty("초급")
                .posture("엎드린자세")
                .bodyPart("가슴")
                .thumbnailUrl("https://example.com/pushup.jpg")
                .build();
        exerciseService.saveExercise(newExercise);

        List<ExerciseDTO> exercises = exerciseService.findAllExercises();
        assertNotNull(exercises);
        assertFalse(exercises.isEmpty());
        // 콘솔에 전체 운동 목록 출력
        System.out.println("전체 운동 목록:");
        exercises.forEach(System.out::println);
    }

    @Test
    void findExerciseById() {
        ExerciseDTO newExercise = ExerciseDTO.builder()
                .name("푸시업")
                .category("근력운동")
                .description("가슴 근육을 강화하는 운동")
                .difficulty("초급")
                .posture("엎드린자세")
                .bodyPart("가슴")
                .thumbnailUrl("https://example.com/pushup.jpg")
                .build();
        Integer exerciseId = exerciseService.saveExercise(newExercise);

        ExerciseDTO foundExercise = exerciseService.findExerciseById(exerciseId);
        assertNotNull(foundExercise);
        assertEquals(exerciseId, foundExercise.getId());
        assertEquals("푸시업", foundExercise.getName());
        System.out.println("조회된 운동: " + foundExercise);
    }

    @Test
    void findExerciseById_NotFound() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            exerciseService.findExerciseById(999999);
        });
        assertTrue(exception.getMessage().contains("Exercise not found"));
        System.out.println("운동 조회 실패 확인됨: " + exception.getMessage());
    }

    @Test
    void deleteExercise() {
        ExerciseDTO newExercise = ExerciseDTO.builder()
                .name("런지")
                .category("근력운동")
                .description("하체 근육을 강화하는 운동")
                .difficulty("초급")
                .posture("선자세")
                .bodyPart("하체")
                .thumbnailUrl("https://example.com/lunge.jpg")
                .build();
        Integer exerciseId = exerciseService.saveExercise(newExercise);
        exerciseService.deleteExercise(exerciseId);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            exerciseService.findExerciseById(exerciseId);
        });
        assertTrue(exception.getMessage().contains("Exercise not found"));
        System.out.println("운동 삭제 확인됨: " + exception.getMessage());
    }
}
