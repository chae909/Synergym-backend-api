package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.RoutineExerciseId;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.repository.RoutineExerciseRepository;
import org.synergym.backendapi.repository.RoutineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineExerciseServiceImpl implements RoutineExerciseService {

    private final RoutineExerciseRepository routineExerciseRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    // 루틴 ID로 Routine 엔티티 조회
    private Routine findRoutineById(int routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_NOT_FOUND));
    }

    // 운동 ID로 Exercise 엔티티 조회
    private Exercise findExerciseById(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    // 복합 키로 RoutineExercise 엔티티 조회
    private RoutineExercise findRoutineExerciseById(RoutineExerciseId id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_EXERCISE_NOT_FOUND));
    }

    /**
     * 루틴에 운동 추가
     */
    @Override
    @Transactional
    public RoutineExerciseDTO addExerciseToRoutine(RoutineExerciseDTO requestDTO) {
        Routine routine = findRoutineById(requestDTO.getRoutineId());
        Exercise exercise = findExerciseById(requestDTO.getExerciseId());

        // 해당 루틴에 포함된 운동의 수를 기준으로 새로운 운동의 순서를 설정
        int newOrder = routineExerciseRepository.findByRoutine(routine).size();

        // RoutineExercise 엔티티 생성
        RoutineExercise routineExercise = RoutineExercise.builder()
                .routine(routine)
                .exercise(exercise)
                .order(newOrder) // 새 운동의 순서
                .build();

        // 저장 후 DTO로 변환하여 반환
        RoutineExercise savedRoutineExercise = routineExerciseRepository.save(routineExercise);
        return entityToDTO(savedRoutineExercise);
    }

    /**
     * 루틴에 포함된 모든 운동 조회
     */
    @Override
    public List<RoutineExerciseDTO> getAllExercisesByRoutineId(int routineId) {
        Routine routine = findRoutineById(routineId);

        // RoutineExercise 리스트를 DTO로 변환
        return routineExerciseRepository.findByRoutine(routine).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 루틴 내 운동 순서 변경
     */
    @Override
    @Transactional
    public RoutineExerciseDTO updateExerciseOrder(int routineId, int exerciseId, int newOrder) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        RoutineExercise routineExercise = findRoutineExerciseById(id);

        // 순서 업데이트
        routineExercise.updateOrder(newOrder);

        // 주의: 순서 충돌은 처리하지 않음
        return entityToDTO(routineExercise);
    }

    /**
     * 루틴에서 운동 제거
     */
    @Override
    @Transactional
    public void removeExerciseFromRoutine(int routineId, int exerciseId) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);

        // 존재 여부 확인 후 삭제
        if (!routineExerciseRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.ROUTINE_EXERCISE_NOT_FOUND);
        }

        routineExerciseRepository.deleteById(id);
    }
}
