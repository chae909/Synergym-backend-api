package org.synergym.backendapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.repository.ExerciseRepository;
import java.util.List;

@SpringBootTest // JPA 테스트에 필요한 설정만 로드
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void saveExercise() {
        Exercise newExercise = Exercise.builder()
                .name("스쿼트")
                .difficulty("초급")
                .bodyPart("하체")
                .description("허리를 펴고 무릎을 굽혀 앉았다 일어나는 운동입니다.")
                .build();

        Exercise savedExercise = exerciseRepository.save(newExercise);

        System.out.println(savedExercise);
    }

    @Test
    void findAllExercises() {
        List<Exercise> exercises = exerciseRepository.findAll();

        System.out.println(exercises);
    }

//    @Test
//    void updateExerciseDifficulty() {
//        Exercise savedExercise = exerciseRepository.save(Exercise.builder().name("턱걸이").difficulty("중급").build());
//
//        Exercise exerciseToUpdate = exerciseRepository.findById(savedExercise.getId()).get();
//        exerciseToUpdate.updateDifficulty("고급");
//
//        Exercise updatedExercise = exerciseRepository.findById(savedExercise.getId()).get();
//
//        System.out.println(updatedExercise);
//    }

    @Test
    void deleteExercise() {
        exerciseRepository.deleteById(8);
    }
}