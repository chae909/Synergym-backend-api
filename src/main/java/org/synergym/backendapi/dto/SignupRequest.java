package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.synergym.backendapi.entity.Role;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    private String email;
    private String password;
    private String name;
    private String goal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String gender;
    private Float weight;
    private Float height;
}
