package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByName(signupRequest.getName())) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        User newUser = requestToEntity(signupRequest);

        if (profileImage != null && !profileImage.isEmpty()) {
            newUser.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        User savedUser = userRepository.save(newUser);

        userRepository.save(savedUser);
    }


    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("일치하는 사용자가 없습니다.")
                    .build();
        }

        User user = optionalUser.get();
        log.info("사용자 찾음: Email={}, Username={}", user.getEmail(), user.getName());

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("비밀번호가 틀렸습니다.")
                    .build();
        }

        return LoginResponse.builder()
                    .id(user.getId())
                    .success(true)
                    .message("로그인 성공")
                    .build();
    }

    @Override
    public String findEmail(FindEmailRequest findEmailRequest) {
        User user = userRepository.findByNameAndBirthday(findEmailRequest.getName(), findEmailRequest.getBirthday())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getEmail();
    }


    // 임시 비밀번호 생성 헬퍼 메서드
    private String getTempPassword() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmailAndName(resetPasswordRequest.getEmail(), resetPasswordRequest.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 임시 비밀번호 생성
        String tempPassword = getTempPassword();

        // 사용자 비밀번호를 임시 비밀번호로 업데이트
        // 나중에 Encoder 적용
        user.updatePassword(tempPassword);

        // 임시 비밀번호 반환
        return tempPassword;
    }
}
