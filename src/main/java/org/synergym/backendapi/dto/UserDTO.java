package org.synergym.backendapi.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private int id;
    private String email;
    @ToString.Exclude
    private String password;
    private String name;
    private String goal;
    private String profileImageUrl;
}
