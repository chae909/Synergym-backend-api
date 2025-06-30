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
import java.util.Optional;
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

    private RoutineExercise findRoutineExerciseById(RoutineExerciseId id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 루틴-운동 관계가 없습니다."));
    }

    private Exercise findExerciseById(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    @Override
    @Transactional
    public RoutineExerciseDTO addExerciseToRoutine(RoutineExerciseDTO requestDTO) {
        Routine routine = findRoutineById(requestDTO.getRoutineId());
        Exercise exercise = findExerciseById(requestDTO.getExerciseId());

        RoutineExercise newRoutineExercise = DTOtoEntity(requestDTO, routine, exercise);
        RoutineExercise saved = routineExerciseRepository.save(newRoutineExercise);

        return entityToDTO(saved);
    }

    @Override
    public RoutineExerciseDTO getRoutineExerciseById(int routineId, int exerciseId) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        RoutineExercise routineExercise = findRoutineExerciseById(id);
        return entityToDTO(routineExercise);
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
        Routine routine = findRoutineById(routineId);

        // 변경하려는 순서(newOrder)에 다른 운동이 있는지 조회
        Optional<RoutineExercise> collidingExerciseOptional = routineExerciseRepository.findByRoutineAndOrder(routine, newOrder);

        // =값이 존재하는지 확인
        if (collidingExerciseOptional.isPresent()) {
            RoutineExercise collidingExercise = collidingExerciseOptional.get();

            if (collidingExercise.getExercise().getId() != exerciseId) {
                // 순서가 충돌된 운동의 순서를 0으로 변경
                collidingExercise.updateOrder(0);
            }
        }

        // 순서를 변경할 대상 운동 조회, 순서 업데이트
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        RoutineExercise routineExerciseToUpdate = findRoutineExerciseById(id);
        routineExerciseToUpdate.updateOrder(newOrder);

        return entityToDTO(routineExerciseToUpdate);
    }

    @Override
    @Transactional
    public void removeExerciseFromRoutine(int routineId, int exerciseId) {
        RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        RoutineExercise routineExercise = findRoutineExerciseById(id);
        routineExerciseRepository.delete(routineExercise);
    }
}
