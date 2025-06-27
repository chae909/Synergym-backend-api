package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.service.ExerciseLikeService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExerciseLikeServiceTest {

    @Autowired
    private ExerciseLikeService exerciseLikeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void saveExerciseLike() {
        User user = userRepository.save(
                User.builder()
                        .email("likeuser@test.com")
                        .name("좋아요유저")
                        .password("pw123")
                        .goal("건강")
                        .build()
        );
        Exercise exercise = exerciseRepository.save(
                Exercise.builder()
                        .name("테스트운동")
                        .category("테스트")
                        .description("테스트용 운동")
                        .difficulty("초급")
                        .posture("선자세")
                        .bodyPart("전신")
                        .thumbnailUrl("https://example.com/test.jpg")
                        .build()
        );
        ExerciseLikeDTO likeDTO = ExerciseLikeDTO.builder()
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .build();

        exerciseLikeService.saveExerciseLike(likeDTO);
        boolean exists = exerciseLikeService.existsByUserAndExercise(user.getId(), exercise.getId());
        System.out.println("Like exists after save: " + exists);
        assertTrue(exists);
    }

    @Test
    void deleteExerciseLike() {
        User user = userRepository.save(
                User.builder()
                        .email("likeuser@test.com")
                        .name("좋아요유저")
                        .password("pw123")
                        .goal("건강")
                        .build()
        );
        Exercise exercise = exerciseRepository.save(
                Exercise.builder()
                        .name("테스트운동")
                        .category("테스트")
                        .description("테스트용 운동")
                        .difficulty("초급")
                        .posture("선자세")
                        .bodyPart("전신")
                        .thumbnailUrl("https://example.com/test.jpg")
                        .build()
        );
        ExerciseLikeDTO likeDTO = ExerciseLikeDTO.builder()
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .build();

        exerciseLikeService.saveExerciseLike(likeDTO);
        exerciseLikeService.deleteExerciseLike(user.getId(), exercise.getId());
        boolean exists = exerciseLikeService.existsByUserAndExercise(user.getId(), exercise.getId());
        System.out.println("Like exists after delete: " + exists);
        assertFalse(exists);
    }

    @Test
    void findLikesByUser() {
        User user = userRepository.save(
                User.builder()
                        .email("likeuser@test.com")
                        .name("좋아요유저")
                        .password("pw123")
                        .goal("건강")
                        .build()
        );
        Exercise exercise = exerciseRepository.save(
                Exercise.builder()
                        .name("테스트운동")
                        .category("테스트")
                        .description("테스트용 운동")
                        .difficulty("초급")
                        .posture("선자세")
                        .bodyPart("전신")
                        .thumbnailUrl("https://example.com/test.jpg")
                        .build()
        );
        ExerciseLikeDTO likeDTO = ExerciseLikeDTO.builder()
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .build();

        exerciseLikeService.saveExerciseLike(likeDTO);
        var likes = exerciseLikeService.findLikesByUser(user.getId());
        System.out.println("Likes by user: " + likes);
        assertFalse(likes.isEmpty());
    }

    @Test
    void findLikesByExercise() {
        User user = userRepository.save(
                User.builder()
                        .email("likeuser@test.com")
                        .name("좋아요유저")
                        .password("pw123")
                        .goal("건강")
                        .build()
        );
        Exercise exercise = exerciseRepository.save(
                Exercise.builder()
                        .name("테스트운동")
                        .category("테스트")
                        .description("테스트용 운동")
                        .difficulty("초급")
                        .posture("선자세")
                        .bodyPart("전신")
                        .thumbnailUrl("https://example.com/test.jpg")
                        .build()
        );
        ExerciseLikeDTO likeDTO = ExerciseLikeDTO.builder()
                .userId(user.getId())
                .exerciseId(exercise.getId())
                .build();

        exerciseLikeService.saveExerciseLike(likeDTO);
        var likes = exerciseLikeService.findLikesByExercise(exercise.getId());
        System.out.println("Likes by exercise: " + likes);
        assertFalse(likes.isEmpty());
    }
}
