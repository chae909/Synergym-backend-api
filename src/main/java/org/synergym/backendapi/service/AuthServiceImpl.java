package org.synergym.backendapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;
import org.synergym.backendapi.util.JwtUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

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
                    .user(entityToDTO(user))
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
        log.info(">>>>> 아이디 찾기 요청 데이터 수신 <<<<<");
        log.info("입력된 이름: '{}'", findEmailRequest.getName());
        log.info("입력된 생년월일: {}", findEmailRequest.getBirthday());
        log.info("생년월일 클래스 타입: {}", findEmailRequest.getBirthday().getClass().getName());
        log.info("------------------------------------");

        // trim()을 사용하여 이름의 앞뒤 공백 제거
        String name = findEmailRequest.getName().trim();
        LocalDate birthday = findEmailRequest.getBirthday();

        User user = userRepository.findByNameAndBirthday(name, birthday)
                .orElseThrow(() -> {
                    log.error(">>>>> DB에서 사용자 조회 실패. 조회 조건:");
                    log.error("이름: '{}', 생년월일: {}", name, birthday);
                    return new IllegalArgumentException("일치하는 사용자 정보가 없습니다.");
                });

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // "synergym" provider가 아닌 경우(소셜 가입자) 예외 발생
        if (user.getProvider() == null || !user.getProvider().equals("synergym")) {
            throw new IllegalStateException("소셜 계정으로 가입한 사용자는 비밀번호를 재설정할 수 없습니다.");
        }

        // 일반 가입자인 경우에만 인증 코드 발송
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + email, verificationCode, Duration.ofMinutes(5));
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

    @Override
    public LoginResponse socialSignUp(SocialSignupRequest signupRequest) {
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isBlank()) {
            throw new IllegalArgumentException("소셜 회원가입 요청에 이메일 정보가 누락되었습니다.");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalStateException("이미 가입된 계정입니다. 일반 로그인을 이용해주세요.");
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .name(signupRequest.getName())
                .password(passwordEncoder.encode("SocialLoginDummyPassword" + UUID.randomUUID()))
                .birthday(signupRequest.getBirthday())
                .role(Role.MEMBER)
                .provider(signupRequest.getProvider())
                .build();

        userRepository.save(user);
        log.info("소셜 회원가입 완료 및 로그인 처리: {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return LoginResponse.builder()
                .user(entityToDTO(user))
                .success(true)
                .message("소셜 회원가입 및 로그인 성공")
                .token(token)
                .build();
    }
}
