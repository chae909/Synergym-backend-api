package org.synergym.backendapi.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.ChangePasswordRequest;
import org.synergym.backendapi.dto.FindEmailRequest;
import org.synergym.backendapi.dto.LoginRequest;
import org.synergym.backendapi.dto.LoginResponse;
import org.synergym.backendapi.dto.ResetPasswordRequest;
import org.synergym.backendapi.dto.SignupRequest;
import org.synergym.backendapi.dto.SocialSignupRequest;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.provider.JwtTokenProvider;
import org.synergym.backendapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String VERIFICATION_CODE_PREFIX = "verification:";

    /**
     * 회원가입 처리
     */
    @Override
    public void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException {
        // 탈퇴한 사용자 포함하여 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmailIncludingDeleted(signupRequest.getEmail());
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // 이미 활성화된 사용자인 경우
            if (user.getUseYn() == 'Y') {
                throw new IllegalStateException("이미 사용 중인 이메일입니다.");
            }
            
            // 탈퇴한 사용자가 재가입하는 경우
            if (user.getUseYn() == 'N') {
                log.info("탈퇴한 사용자가 재가입합니다: {}", signupRequest.getEmail());
                
                // 사용자 정보 업데이트
                user.reactivate(); // useYn을 Y로 변경
                user.updatePassword(passwordEncoder.encode(signupRequest.getPassword()));
                user.updateName(signupRequest.getName());
                user.updateGoal(signupRequest.getGoal());
                user.updateBirthday(signupRequest.getBirthday());
                user.updateGender(signupRequest.getGender());
                user.updateWeight(signupRequest.getWeight());
                user.updateHeight(signupRequest.getHeight());
                user.updateProvider("synergym");
                
                // 프로필 이미지 설정
                if (profileImage != null && !profileImage.isEmpty()) {
                    user.updateProfileImage(
                            profileImage.getBytes(),
                            profileImage.getOriginalFilename(),
                            profileImage.getContentType()
                    );
                } else {
                    user.removeProfileImage(); // 기존 프로필 이미지 제거
                }
                
                userRepository.save(user);
                log.info("재가입 완료: {}", signupRequest.getEmail());
                return;
            }
        }

        // 활성화된 사용자 중 닉네임 중복 체크
        if (userRepository.existsByName(signupRequest.getName())) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        // 신규 사용자 생성
        User newUser = dtoToEntity(signupRequest, this.passwordEncoder);

        // 프로필 이미지 설정
        if (profileImage != null && !profileImage.isEmpty()) {
            newUser.updateProfileImage(
                    profileImage.getBytes(),
                    profileImage.getOriginalFilename(),
                    profileImage.getContentType()
            );
        }

        // 사용자 저장
        userRepository.save(newUser);
    }

    /**
     * 로그인 처리
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // 스프링 시큐리티를 통한 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // 인증 후 JWT 토큰 발급
            String token = jwtTokenProvider.generateToken(authentication);

            // 사용자 정보 조회
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("인증 후 사용자를 찾을 수 없습니다."));

            // 로그인 성공 응답 반환
            return LoginResponse.builder()
                    .user(entityToDTO(user))
                    .success(true)
                    .message("로그인 성공")
                    .token(token)
                    .build();

        } catch (Exception e) {
            // 로그인 실패 처리
            log.error("로그인 실패: 이메일 또는 비밀번호 불일치. {}", e.getMessage());
            return LoginResponse.builder()
                    .success(false)
                    .message("이메일 또는 비밀번호가 일치하지 않습니다.")
                    .build();
        }
    }

    /**
     * 이메일 중복 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 이름과 생년월일로 이메일 찾기
     */
    @Override
    @Transactional(readOnly = true)
    public String findEmail(FindEmailRequest findEmailRequest) {
        log.info(">>>>> 아이디 찾기 요청 데이터 수신 <<<<<");
        log.info("입력된 이름: '{}'", findEmailRequest.getName());
        log.info("입력된 생년월일: {}", findEmailRequest.getBirthday());

        String name = findEmailRequest.getName().trim();
        LocalDate birthday = findEmailRequest.getBirthday();

        // DB에서 사용자 검색
        User user = userRepository.findByNameAndBirthday(name, birthday)
                .orElseThrow(() -> {
                    log.error(">>>>> DB에서 사용자 조회 실패. 이름: '{}', 생년월일: {}", name, birthday);
                    return new IllegalArgumentException("일치하는 사용자 정보가 없습니다.");
                });

        return user.getEmail();
    }

    /**
     * 이메일과 이름으로 임시 비밀번호 발급
     */
    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // 사용자 존재 여부 확인
        User user = userRepository.findByEmailAndName(resetPasswordRequest.getEmail(), resetPasswordRequest.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 임시 비밀번호 생성 및 업데이트
        String tempPassword = getTempPassword();
        user.updatePassword(passwordEncoder.encode(tempPassword));

        // 이메일 발송
        emailService.sendVerificationEmail(user.getEmail(), "임시 비밀번호: " + tempPassword);

        return "이메일로 임시 비밀번호가 발송되었습니다.";
    }

    /**
     * 이메일 인증 코드 발송
     */
    @Override
    public void sendVerificationCode(String email) {
        // 사용자 검증
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 소셜 가입자는 인증 코드 발송 불가
//        if (user.getProvider() == null || !user.getProvider().equals("synergym")) {
//            throw new IllegalStateException("소셜 계정으로 가입한 사용자는 비밀번호를 재설정할 수 없습니다.");
//        }

        // 인증 코드 생성 및 Redis 저장 (5분 유효)
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + email, verificationCode, Duration.ofMinutes(5));

        // 이메일 발송
        emailService.sendVerificationEmail(email, "인증 코드: " + verificationCode);
    }

    /**
     * 인증 코드 검증
     */
    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
        if (storedCode != null && storedCode.equals(code)) {
            // 일치 시 Redis에서 인증 코드 삭제
            redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);
            return true;
        }
        return false;
    }

    /**
     * 비밀번호 변경
     */
    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.updatePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }

    /**
     * 임시 비밀번호 생성기 (UUID 기반 10자리)
     */
    private String getTempPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * 소셜 회원가입 및 로그인 처리
     */
    @Override
    public LoginResponse socialSignUp(SocialSignupRequest signupRequest) {
        if (signupRequest.getEmail() == null || signupRequest.getEmail().isBlank()) {
            throw new IllegalArgumentException("소셜 회원가입 요청에 이메일 정보가 누락되었습니다.");
        }

        // 탈퇴한 사용자 포함하여 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmailIncludingDeleted(signupRequest.getEmail());
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            
            // 이미 활성화된 사용자인 경우
            if (user.getUseYn() == 'Y') {
                throw new IllegalStateException("이미 가입된 계정입니다. 일반 로그인을 이용해주세요.");
            }
            
            // 탈퇴한 사용자가 재가입하는 경우
            if (user.getUseYn() == 'N') {
                log.info("탈퇴한 사용자가 소셜 재가입합니다: {}", signupRequest.getEmail());
                
                // 사용자 정보 업데이트
                user.reactivate(); // useYn을 Y로 변경
                user.updateName(signupRequest.getName());
                user.updateBirthday(signupRequest.getBirthday());
                user.updateProvider(signupRequest.getProvider());
                user.updatePassword(passwordEncoder.encode("SocialLoginDummyPassword" + UUID.randomUUID()));
                
                userRepository.save(user);
                log.info("소셜 재가입 완료: {}", signupRequest.getEmail());
            }
        } else {
            // 신규 사용자 생성
            user = User.builder()
                    .email(signupRequest.getEmail())
                    .name(signupRequest.getName())
                    .password(passwordEncoder.encode("SocialLoginDummyPassword" + UUID.randomUUID()))
                    .birthday(signupRequest.getBirthday())
                    .role(Role.MEMBER)
                    .provider(signupRequest.getProvider())
                    .build();

            userRepository.save(user);
            log.info("소셜 회원가입 완료 및 로그인 처리: {}", user.getEmail());
        }

        // 소셜 로그인용 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                new UserDetailsServiceImpl(userRepository).loadUserByUsername(user.getEmail()).getAuthorities()
        );

        // 토큰 발급
        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .user(entityToDTO(user))
                .success(true)
                .message("소셜 회원가입 및 로그인 성공")
                .token(token)
                .build();
    }
}
