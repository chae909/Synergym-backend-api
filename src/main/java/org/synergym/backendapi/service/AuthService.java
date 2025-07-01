package org.synergym.backendapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;

import java.io.IOException;

public interface AuthService {

    void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException;
    LoginResponse login(LoginRequest loginRequest);
    boolean checkEmailExists(String email);
    String findEmail(FindEmailRequest findEmailRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    void sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    LoginResponse socialSignUp(SocialSignupRequest socialSignupRequest);

    default User dtoToEntity(SignupRequest signupRequest, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
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
