package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.RoutineDTO;
import org.synergym.backendapi.dto.RoutineExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.ExerciseRepository;
import org.synergym.backendapi.repository.RoutineExerciseRepository;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineExerciseRepository routineExerciseRepository;
    private final ExerciseRepository exerciseRepository;

    // ID로 사용자 조회 (없으면 예외 발생)
    private User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
    
    // ID로 루틴 조회 (없으면 예외 발생)
    private Routine findRoutineById(int routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_NOT_FOUND));
    }

    // ID로 운동 조회 (없으면 예외 발생)
    private Exercise findExerciseById(int exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    // 루틴 생성
    @Override
    @Transactional
    public RoutineDTO createRoutine(RoutineDTO routineDTO, int userId) {
        User user = findUserById(userId);
        // DTOtoEntity는 Routine의 name, description, user만 설정한다고 가정합니다.
        Routine newRoutine = DTOtoEntity(routineDTO, user);
        Routine savedRoutine = routineRepository.save(newRoutine);

        List<RoutineExercise> savedRoutineExercises = new ArrayList<>();
        if (routineDTO.getExercises() != null && !routineDTO.getExercises().isEmpty()) {
            List<RoutineExerciseDTO> exerciseDTOs = routineDTO.getExercises();
            for (int i = 0; i < exerciseDTOs.size(); i++) {
                RoutineExerciseDTO exerciseDTO = exerciseDTOs.get(i);
                Exercise exercise = findExerciseById(exerciseDTO.getExerciseId());
                RoutineExercise routineExercise = RoutineExercise.builder()
                        .routine(savedRoutine)
                        .exercise(exercise)
                        .order(i)
                        .build();
                savedRoutineExercises.add(routineExerciseRepository.save(routineExercise));
            }
        }

        savedRoutine.updateExercises(savedRoutineExercises);

        return entityToDTO(savedRoutine, savedRoutineExercises);

    }

    // 루틴 상세 조회
    @Override
    public RoutineDTO getRoutineDetails(int routineId) {
        Routine routine = findRoutineById(routineId);
        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
        return entityToDTO(routine, exercises);
    }

    // 사용자별 루틴 목록 조회
    @Override
    public List<RoutineDTO> getRoutinesByUserId(int userId) {
        User user = findUserById(userId); // userId로 User 객체 조회
        List<Routine> routines = routineRepository.findByUser(user); // 해당 User의 모든 루틴 조회

        return routines.stream()
                .map(routine -> {
                    List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine); // 각 루틴에 속한 운동 목록 조회
                    return entityToDTO(routine, exercises); // DTO로 변환
                })
                .collect(Collectors.toList());
    }

    // 전체 루틴 목록 조회
    @Override
    public List<RoutineDTO> getAllRoutines() {
        return routineRepository.findAll().stream()
                .map(routine -> {
                    List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
                    return entityToDTO(routine, exercises);
                })
                .collect(Collectors.toList());
    }

    // 루틴 삭제
    @Override
    @Transactional
    public RoutineDTO updateRoutine(int routineId, RoutineDTO routineDTO) {
        Routine routine = findRoutineById(routineId);
        routine.updateName(routineDTO.getName());
        routine.updateDescription(routineDTO.getDescription());

        // 기존 운동 목록을 삭제합니다.
        List<RoutineExercise> oldExercises = routineExerciseRepository.findByRoutine(routine);
        routineExerciseRepository.deleteAll(oldExercises);

        // DTO로부터 새로운 운동 목록을 생성하고 저장합니다.
        List<RoutineExercise> newRoutineExercises = new ArrayList<>();
        if (routineDTO.getExercises() != null && !routineDTO.getExercises().isEmpty()) {
            List<RoutineExerciseDTO> exerciseDTOs = routineDTO.getExercises();
            for (int i = 0; i < exerciseDTOs.size(); i++) {
                RoutineExerciseDTO exerciseDTO = exerciseDTOs.get(i);
                Exercise exercise = findExerciseById(exerciseDTO.getExerciseId());
                RoutineExercise routineExercise = RoutineExercise.builder()
                        .routine(routine)
                        .exercise(exercise)
                        .order(i)
                        .build();
                newRoutineExercises.add(routineExerciseRepository.save(routineExercise));
            }
        }

        // 메모리의 루틴 객체에 새로운 운동 목록을 반영
        routine.updateExercises(newRoutineExercises);

        // 업데이트된 루틴 정보를 DTO로 변환하여 반환
        return entityToDTO(routine, newRoutineExercises);
    }

    // 루틴 수정
    @Override
    @Transactional
    public void deleteRoutine(int routineId) {
        Routine routine = findRoutineById(routineId);

        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
        routineExerciseRepository.deleteAll(exercises);

        routine.softDelete();
    }

    // 루틴 이름으로 검색
    @Override
    public List<RoutineDTO> getRoutinesByName(String name) {
        return routineRepository.findByName(name).stream()
                .map(routine -> {
                    List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
                    return entityToDTO(routine, exercises);
                })
                .collect(Collectors.toList());
    }
}