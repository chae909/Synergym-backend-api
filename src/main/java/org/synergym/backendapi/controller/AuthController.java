package org.synergym.backendapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.LoginRequest;
import org.synergym.backendapi.dto.LoginResponse;
import org.synergym.backendapi.dto.SignupRequest;
import org.synergym.backendapi.dto.UserDTO;
import org.synergym.backendapi.service.AuthService;
import org.synergym.backendapi.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/signup", consumes = { "multipart/form-data" })
    public ResponseEntity<String> signUp(@RequestPart("signupRequest") SignupRequest signupRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        log.info("회원가입 요청: {}", signupRequest.getEmail());
        authService.signUp(signupRequest, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("로그인 요청: Email={}", loginRequest.getEmail());
        LoginResponse loginResponse = authService.login(loginRequest);
        log.info("로그인 결과: success={}, message={}, id={}",
                loginResponse.isSuccess(),
                loginResponse.getMessage(),
                loginResponse.getId());

        return ResponseEntity.ok(loginResponse);
    }
}
