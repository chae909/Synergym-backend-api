package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 로그인 응답 DTO
public class LoginResponse {
    private UserDTO user; // 유저정보
    private String token; // 토큰
    private boolean success; // 성공여부
    private String message; // 메세지
}
