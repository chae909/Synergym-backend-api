package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;

import java.util.List;

public interface RoutineExerciseService {

    RoutineExerciseDTO addExerciseToRoutine(RoutineExerciseDTO requestDTO);
    RoutineExerciseDTO getRoutineExerciseById(int routineId, int exerciseId);
    List<RoutineExerciseDTO> getAllExercisesByRoutineId(int routineId);
    RoutineExerciseDTO updateExerciseOrder(int routineId, int exerciseId, int newOrder);
    void removeExerciseFromRoutine(int routineId, int exerciseId);

    default RoutineExercise DTOtoEntity(RoutineExerciseDTO dto, Routine routine, Exercise exercise) {
        return RoutineExercise.builder()
                .routine(routine)
                .exercise(exercise)
                .order(dto.getOrder())
                .build();
    }

    default RoutineExerciseDTO entityToDTO(RoutineExercise re){
        return RoutineExerciseDTO.builder()
                .routineId(re.getRoutine().getId())
                .exerciseId(re.getExercise().getId())
                .exerciseName(re.getExercise().getName())
                .order(re.getOrder())
                .build();
    }
}
