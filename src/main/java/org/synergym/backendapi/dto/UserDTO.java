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
public class UserDTO {

    private int id;
    private String email;
    @ToString.Exclude
    @JsonIgnore  // API 응답에서 비밀번호 제외
    private String password;
    private String name;
    private String goal;
    private LocalDate birthday;
    private String gender;
    private Float weight;
    private Float height;
    private Role role;
    private String profileImageUrl;
}