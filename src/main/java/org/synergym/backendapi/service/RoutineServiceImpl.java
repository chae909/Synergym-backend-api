package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.RoutineDTO;
import org.synergym.backendapi.entity.Routine;
import org.synergym.backendapi.entity.RoutineExercise;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.RoutineExerciseRepository;
import org.synergym.backendapi.repository.RoutineRepository;
import org.synergym.backendapi.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutineExerciseRepository routineExerciseRepository;

    private User findUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Routine findRoutineById(int routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROUTINE_NOT_FOUND));
    }

    @Override
    @Transactional
    public RoutineDTO createRoutine(RoutineDTO routineDTO, int userId) {
        User user = findUserById(userId);
        Routine newRoutine = DTOtoEntity(routineDTO, user);
        Routine savedRoutine = routineRepository.save(newRoutine);
        return entityToDTO(savedRoutine, List.of());
    }

    @Override
    public RoutineDTO getRoutineDetails(int routineId) {
        Routine routine = findRoutineById(routineId);
        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
        return entityToDTO(routine, exercises);
    }

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

    @Override
    public List<RoutineDTO> getAllRoutines() {
        return routineRepository.findAll().stream()
                .map(routine -> {
                    List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
                    return entityToDTO(routine, exercises);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoutineDTO updateRoutine(int routineId, RoutineDTO routineDTO) {
        Routine routine = findRoutineById(routineId);
        routine.updateName(routineDTO.getName());
        routine.updateDescription(routineDTO.getDescription());
        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
        return entityToDTO(routine, exercises);
    }

    @Override
    @Transactional
    public void deleteRoutine(int routineId) {
        Routine routine = findRoutineById(routineId);

        List<RoutineExercise> exercises = routineExerciseRepository.findByRoutine(routine);
        routineExerciseRepository.deleteAll(exercises);

        routine.softDelete();
    }

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
