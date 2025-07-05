package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseLikeDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.ExerciseLikeId;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
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
    private final NotificationService notificationService;

    private User findUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Exercise findExerciseById(int id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    @Override
    @Transactional
    public void add(ExerciseLikeDTO exerciseLikeDTO) {
        User user = findUserById(exerciseLikeDTO.getUserId());
        Exercise exercise = findExerciseById(exerciseLikeDTO.getExerciseId());
        
        // 중복 좋아요 방지
        if (isLiked(exerciseLikeDTO.getUserId(), exerciseLikeDTO.getExerciseId())) {
            throw new IllegalStateException("이미 좋아요한 운동입니다.");
        }
        
        ExerciseLike like = DTOtoEntity(exerciseLikeDTO, user, exercise);
        exerciseLikeRepository.save(like);
        
        // 운동 좋아요 알림 생성
        notificationService.createExerciseLikeNotification(exerciseLikeDTO.getExerciseId(), exerciseLikeDTO.getUserId());
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer exerciseId) {
        // 사용자와 운동 존재 여부 확인
        findUserById(userId);
        findExerciseById(exerciseId);
        
        ExerciseLikeId id = new ExerciseLikeId(userId, exerciseId);
        
        if (!exerciseLikeRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.EXERCISE_LIKE_NOT_FOUND);
        }
        
        exerciseLikeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLikeDTO> getByUser(Integer userId) {
        findUserById(userId); // 사용자 존재 여부 확인
        
        return exerciseLikeRepository.findByUserId(userId).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseLikeDTO> getByExercise(Integer exerciseId) {
        findExerciseById(exerciseId); // 운동 존재 여부 확인
        
        return exerciseLikeRepository.findByExerciseId(exerciseId).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Integer userId, Integer exerciseId) {
        // 사용자와 운동이 존재하는지 먼저 확인
        findUserById(userId);
        findExerciseById(exerciseId);
        
        // 좋아요 존재 여부 확인
        return exerciseLikeRepository.existsByUserIdAndExerciseId(userId, exerciseId);
    }
} 