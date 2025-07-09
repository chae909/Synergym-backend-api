package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 루틴 관련 DTO
public class RoutineDTO {

    private int id; // 루틴 고유 id
    private String name; // 루틴 이름
    private String description; // 루틴 설명
    private Integer userId; // 루틴 생성 사용자 id
    private List<RoutineExerciseDTO> exercises; // 루틴에 들어간 운동 list
}
