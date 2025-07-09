package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private UserDTO user; // 유저정보
    private String token; // 토큰
    private boolean success; // 성공여부
    private String message; // 메세지
}
