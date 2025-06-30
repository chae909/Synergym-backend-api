package org.synergym.backendapi.service;

import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;

import java.io.IOException;

public interface AuthService {

    void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException;
    LoginResponse login(LoginRequest loginRequest);
    String findEmail(FindEmailRequest findEmailRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    default User requestToEntity(SignupRequest signupRequest) {
        return User.builder()
                .email(signupRequest.getEmail())
                .password(signupRequest.getPassword())
                .name(signupRequest.getName())
                .goal(signupRequest.getGoal())
                .birthday(signupRequest.getBirthday())
                .gender(signupRequest.getGender())
                .weight(signupRequest.getWeight())
                .height(signupRequest.getHeight())
                .role(Role.MEMBER)
                .build();
    }
}
