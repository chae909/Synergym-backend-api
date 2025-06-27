package org.synergym.backendapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExerciseLogRoutineId implements Serializable {
    private Integer exerciseLogId;
    private Integer routineId;
} 