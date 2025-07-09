package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.synergym.backendapi.entity.Role;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

// 사용자 관련 DTO
public class UserDTO {

    private int id; // 사용자 ID
    private String email; // 이메일
    @ToString.Exclude
    @JsonIgnore  // API 응답에서 비밀번호 제외
    private String password; // 비밀번호
    private String name; // 이름
    private String goal; // 목표
    private LocalDate birthday; // 생일
    private String gender; // 성별
    private Float weight; // 몸무게
    private Float height; // 키
    private Role role; // 역할
    private String profileImageUrl; // 프로필 이미지 URL
}