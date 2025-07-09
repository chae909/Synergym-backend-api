package org.synergym.backendapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// 로그인 요청 DTO
public class LoginRequest {
    @NotBlank(message = "이메일은 필수입니다.")
    private String email; // 로그인 - 이메일(아이디)

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password; // 비밀번호
}
