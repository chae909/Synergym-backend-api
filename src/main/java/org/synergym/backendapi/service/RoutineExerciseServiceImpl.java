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

    private Routine findRoutineById(int routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_NOT_FOUND));
    }

    private Exercise findExerciseById(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }
    
    private RoutineExercise findRoutineExerciseById(RoutineExerciseId id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_EXERCISE_NOT_FOUND));
    }

    @Override
    @Transactional
    public RoutineExerciseDTO addExerciseToRoutine(RoutineExerciseDTO requestDTO) {
        Routine routine = findRoutineById(requestDTO.getRoutineId());
        Exercise exercise = findExerciseById(requestDTO.getExerciseId());

        // 루틴에 포함된 기존 운동 개수를 세어 새 운동의 순서를 정합니다.
        int newOrder = routineExerciseRepository.findByRoutine(routine).size();

        RoutineExercise routineExercise = RoutineExercise.builder()
                .routine(routine)
                .exercise(exercise)
                .order(newOrder) // 계산된 순서를 사용합니다.
                .build();

        RoutineExercise savedRoutineExercise = routineExerciseRepository.save(routineExercise);

        return entityToDTO(savedRoutineExercise);
    }

    @Override
    public List<RoutineExerciseDTO> getAllExercisesByRoutineId(int routineId) {
        Routine routine = findRoutineById(routineId);
        return routineExerciseRepository.findByRoutine(routine).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoutineExerciseDTO updateExerciseOrder(int routineId, int exerciseId, int newOrder) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        RoutineExercise routineExercise = findRoutineExerciseById(id);

        routineExercise.updateOrder(newOrder);
        // 참고: 이 로직은 순서 충돌을 처리하지 않습니다.
        // 특정 순서로 변경 시, 해당 순서에 이미 다른 운동이 있다면 추가적인 처리가 필요할 수 있습니다.

        return entityToDTO(routineExercise);
    }

    @Override
    @Transactional
    public void removeExerciseFromRoutine(int routineId, int exerciseId) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        // findById로 존재 확인 후 삭제하는 것이 더 안전하지만, 컨트롤러에서 바로 호출하므로 바로 삭제합니다.
        if (!routineExerciseRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorCode.ROUTINE_EXERCISE_NOT_FOUND);
        }
        routineExerciseRepository.deleteById(id);
    }
}