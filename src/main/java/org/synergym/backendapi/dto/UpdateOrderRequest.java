package org.synergym.backendapi.dto;

import lombok.Data;

@Data

// 루틴에 추가된 운동 순서 변경 요청 DTO
public class UpdateOrderRequest {
    private int order;
}
