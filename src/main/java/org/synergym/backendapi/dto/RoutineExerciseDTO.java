package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineExerciseDTO {

    private Integer id;
    private Integer routineId;
    private Integer exerciseId;
    private String exerciseName;
    private Integer order;
    private Character checkYn;

}
