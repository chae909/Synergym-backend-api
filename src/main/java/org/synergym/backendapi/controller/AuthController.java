package org.synergym.backendapi.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.service.AuthService;
import org.synergym.backendapi.service.UserService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 일반 회원가입
    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public ResponseEntity<String> signUp(@RequestPart("signupRequest") @Valid SignupRequest signupRequest, // @Valid 추가
                                         @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        log.info("회원가입 요청: {}", signupRequest.getEmail());
        authService.signUp(signupRequest, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: Email={}", loginRequest.getEmail());
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("로그인 결과: success={}, message={}, userid={}",
                loginResponse.isSuccess(),
                loginResponse.getMessage(),
                loginResponse.getUser() != null ? loginResponse.getUser().getId() : "N/A");

        return ResponseEntity.ok(loginResponse);
    }

    // 이메일 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = authService.checkEmailExists(email);
        // 응답을 JSON 형식 {"exists": true/false} 로 내려주기 위해 Map 사용
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 이메일 찾기
    @PostMapping("/find-email")
    public ResponseEntity<String> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        log.info("이메일 찾기 요청: name={}", findEmailRequest.getName());
        String email = authService.findEmail(findEmailRequest);
        return ResponseEntity.ok(email);
    }

    // 회원가입시 인증번호 발송
    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        log.info("인증번호 발송 요청: {}", email);
        authService.sendVerificationCode(email);
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    // 회원가입시 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        log.info("인증번호 검증 요청: email={}, code={}", email, code);
        boolean isVerified = authService.verifyCode(email, code);
        return ResponseEntity.ok(Map.of("verified", isVerified));
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        log.info("비밀번호 변경 요청: email={}", changePasswordRequest.getEmail());
        authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // 소셜 회원가입
    @PostMapping("/social-signup")
    public ResponseEntity<LoginResponse> socialSignUp(@RequestBody @Valid SocialSignupRequest socialSignupRequest) {
        LoginResponse response = authService.socialSignUp(socialSignupRequest);
        return ResponseEntity.ok(response);
    }
}
