package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.synergym.backendapi.entity.RoutineExercise;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineDTO {

    private int id;
    private String name;
    private String description;
    private Integer userId;
    private List<RoutineExercise> exercises;
}
