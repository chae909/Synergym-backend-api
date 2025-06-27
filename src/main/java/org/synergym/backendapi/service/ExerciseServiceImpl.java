package org.synergym.backendapi.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.synergym.backendapi.dto.ExerciseDTO;
import org.synergym.backendapi.entity.Exercise;
import org.synergym.backendapi.repository.ExerciseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    @Override
    public Integer saveExercise(ExerciseDTO exerciseDto) {
        Exercise exercise = dtoToEntity(exerciseDto);
        exercise = exerciseRepository.save(exercise);
        return exercise.getId();
    }

    @Transactional
    @Override
    public List<ExerciseDTO> findAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ExerciseDTO findExerciseById(Integer id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return entityToDto(exercise);
    }


    @Transactional
    @Override
    public void deleteExercise(Integer id) {
        exerciseRepository.deleteById(id);
    }
}