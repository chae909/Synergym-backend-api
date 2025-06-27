package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.ExerciseLikeId;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.ExerciseLikeRepository;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseLikeServiceImpl implements ExerciseLikeService {
    private final ExerciseLikeRepository exerciseLikeRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    @Override
    public void saveExerciseLike(ExerciseLikeDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        ExerciseLike like = dtoToEntity(dto, user, exercise);
        exerciseLikeRepository.save(like);
    }

    @Transactional
    @Override
    public void deleteExerciseLike(Integer userId, Integer exerciseId) {
        ExerciseLikeId id = new ExerciseLikeId(userId, exerciseId);
        exerciseLikeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExerciseLikeDTO> findLikesByUser(Integer userId) {
        return exerciseLikeRepository.findAll().stream()
                .filter(like -> Integer.valueOf(like.getUser().getId()).equals(userId))
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExerciseLikeDTO> findLikesByExercise(Integer exerciseId) {
        return exerciseLikeRepository.findAll().stream()
                .filter(like -> Integer.valueOf(like.getExercise().getId()).equals(exerciseId))
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByUserAndExercise(Integer userId, Integer exerciseId) {
        ExerciseLikeId id = new ExerciseLikeId(userId, exerciseId);
        return exerciseLikeRepository.existsById(id);
    }

} 