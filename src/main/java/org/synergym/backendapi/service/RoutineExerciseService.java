package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;

import java.util.List;

public interface RoutineExerciseService {

    /**
     * 루틴에 운동을 추가합니다.
     * @param requestDTO 루틴에 추가할 운동 정보 (routineId, exerciseId, order 등)
     * @return 추가된 루틴 운동 정보 DTO
     */
    RoutineExerciseDTO addExerciseToRoutine(RoutineExerciseDTO requestDTO);

    /**
     * 특정 루틴에 포함된 모든 운동 목록을 조회합니다.
     * @param routineId 조회할 루틴 ID
     * @return 해당 루틴의 운동 목록
     */
    List<RoutineExerciseDTO> getAllExercisesByRoutineId(int routineId);

    /**
     * 루틴 내 특정 운동의 순서를 변경합니다.
     * @param routineId 루틴 ID
     * @param exerciseId 운동 ID
     * @param newOrder 새 순서 값
     * @return 순서가 변경된 운동 정보 DTO
     */
    RoutineExerciseDTO updateExerciseOrder(int routineId, int exerciseId, int newOrder);

    /**
     * 루틴에서 특정 운동을 제거합니다.
     * @param routineId 루틴 ID
     * @param exerciseId 삭제할 운동 ID
     */
    void removeExerciseFromRoutine(int routineId, int exerciseId);

    //DTO → Entity 변환 (빌더 패턴 활용)
    default RoutineExercise DTOtoEntity(RoutineExerciseDTO dto, Routine routine, Exercise exercise) {
        return RoutineExercise.builder()
                .routine(routine)
                .exercise(exercise)
                .order(dto.getOrder())
                .build();
    }

    //Entity → DTO 변환
    default RoutineExerciseDTO entityToDTO(RoutineExercise re){
        return RoutineExerciseDTO.builder()
                .routineId(re.getRoutine().getId())
                .exerciseId(re.getExercise().getId())
                .exerciseName(re.getExercise().getName())
                .order(re.getOrder())
                .build();
    }
}
