package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FindEmailRequest {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name; // 이메일찾기 - 이름

    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday; // 이메일찾기 - 생일
}
