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
        return routineExerciseRepository.findById(id)
                .map(this::entityToDTO)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴-운동 관계를 찾을 수 없습니다."));
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
        RoutineExercise routineExercise = routineExerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 루틴-운동 관계를 찾을 수 없습니다."));

        routineExercise.updateOrder(newOrder);

        return entityToDTO(routineExercise);
    }

    @Override
    @Transactional
    public void removeExerciseFromRoutine(int routineId, int exerciseId) {
    RoutineExerciseId id = new RoutineExerciseId(routineId, exerciseId);
        if (!routineExerciseRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 루틴-운동 관계가 없습니다.");
        }
        routineExerciseRepository.deleteById(id);
    }
}
