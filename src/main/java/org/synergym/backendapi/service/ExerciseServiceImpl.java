package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;
import org.synergym.backendapi.repository.ExerciseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    
    private final ExerciseRepository exerciseRepository;

    private Exercise findExerciseById(int id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    @Override
    @Transactional
    public Integer createExercise(ExerciseDTO exerciseDTO) {
        Exercise exercise = DTOtoEntity(exerciseDTO);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return savedExercise.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseDTO getExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EXERCISE_NOT_FOUND));
        return entityToDTO(exercise);

    }

    @Override
    @Transactional
    public void deleteExercise(Integer id) {
        Exercise exercise = findExerciseById(id);
        exerciseRepository.delete(exercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> getExercisesByName(String name) {
        return exerciseRepository.findByNameContaining(name)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseDTO> getExercisesByCategory(String category) {
        return exerciseRepository.findByCategory(category)
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }
}