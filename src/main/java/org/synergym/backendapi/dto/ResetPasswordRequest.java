package org.synergym.backendapi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

// 비밀번호 재설정 요청 DTO
public class ResetPasswordRequest {
    private String email; // 비밀번호 재설정 - 이메일
    private String name; // 비밀번호 재설정 - 이름
}
