package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialSignupRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email; // 소셜회원가입 - 이메일(아이디)

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name; // 소셜회원가입 - 이름

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String provider;
}
