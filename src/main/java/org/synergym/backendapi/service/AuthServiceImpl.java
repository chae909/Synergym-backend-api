package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.util.JwtUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "verification:";

    @Override
    public void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByName(signupRequest.getName())) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        User newUser = dtoToEntity(signupRequest, this.passwordEncoder);

        if (profileImage != null && !profileImage.isEmpty()) {
            newUser.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        userRepository.save(newUser);
    }


    @Override
    @Transactional(readOnly = true)
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

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("비밀번호가 틀렸습니다.")
                    .build();
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return LoginResponse.builder()
                    .id(user.getId())
                    .success(true)
                    .message("로그인 성공")
                    .token(token)
                    .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public String findEmail(FindEmailRequest findEmailRequest) {
        User user = userRepository.findByNameAndBirthday(findEmailRequest.getName(), findEmailRequest.getBirthday())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getEmail();
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByEmailAndName(resetPasswordRequest.getEmail(), resetPasswordRequest.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String tempPassword = getTempPassword();
        user.updatePassword(passwordEncoder.encode(tempPassword));

        emailService.sendVerificationEmail(user.getEmail(), "임시 비밀번호: " + tempPassword);

        return "이메일로 임시 비밀번호가 발송되었습니다.";
    }

    @Override
    public void sendVerificationCode(String email) {
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                verificationCode,
                Duration.ofMinutes(5)
        );
        emailService.sendVerificationEmail(email, "인증 코드: " + verificationCode);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);
            return true;
        }
        return false;
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updatePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }

    private String getTempPassword() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
