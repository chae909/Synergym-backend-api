package org.synergym.backendapi.service;

import org.synergym.backendapi.dto.RoutineDTO;
import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public interface RoutineService {

    // 루틴 생성
    RoutineDTO createRoutine(RoutineDTO routineDTO, int userId);

    // 챗봇: 신규 루틴 생성 + 운동 추가 (트랜잭션)
    RoutineDTO createRoutineWithExercise(RoutineDTO routineDTO, int userId, int exerciseId, int order);

    // 루틴 상세 조회
    RoutineDTO getRoutineDetails(int routineId);

    // 사용자별 루틴 목록 조회
    List<RoutineDTO> getRoutinesByUserId(int userId);

    // 전체 루틴 목록 조회
    List<RoutineDTO> getAllRoutines();

    // 루틴 수정
    RoutineDTO updateRoutine(int routineId, RoutineDTO routineDTO);

    // 루틴 삭제
    void deleteRoutine(int routineId);

    // 루틴 이름으로 검색
    List<RoutineDTO> getRoutinesByName(String name);

    // DTO → Entity 변환
    default Routine DTOtoEntity(RoutineDTO dto, User user) {
        return Routine.builder()
                .user(user)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    // Entity → DTO 변환
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
