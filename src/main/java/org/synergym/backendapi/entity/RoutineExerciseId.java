package org.synergym.backendapi.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RoutineExerciseId implements Serializable {
    private Integer routineId; // int 대신 Wrapper 클래스인 Integer 사용
    private Integer exerciseId;
}