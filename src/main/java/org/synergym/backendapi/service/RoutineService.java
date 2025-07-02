package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.RoutineDTO;
import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public interface RoutineService {

    RoutineDTO createRoutine(RoutineDTO routineDTO, int userId);
    RoutineDTO getRoutineDetails(int routineId);
    List<RoutineDTO> getRoutinesByUserId(int userId);
    List<RoutineDTO> getAllRoutines();
    RoutineDTO updateRoutine(int routineId, RoutineDTO routineDTO);
    void deleteRoutine(int routineId);
    List<RoutineDTO> getRoutinesByName(String name);

    default Routine DTOtoEntity(RoutineDTO dto, User user) {
        return Routine.builder()
                .user(user)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    default RoutineDTO entityToDTO(Routine routine, List<RoutineExercise> exercises) {
        List<RoutineExerciseDTO> exerciseDTOs = exercises.stream()
                .map(re -> RoutineExerciseDTO.builder()
                        .exerciseId(re.getExercise().getId())
                        .exerciseName(re.getExercise().getName())
                        .order(re.getOrder())
                        .build())
                .collect(Collectors.toList());

        return RoutineDTO.builder()
                .id(routine.getId())
                .name(routine.getName())
                .description(routine.getDescription())
                .userId(routine.getUser().getId())
                .exercises(exerciseDTOs) // 변환된 DTO 리스트 사용
                .build();
    }

}
