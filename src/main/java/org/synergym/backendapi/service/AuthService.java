package org.synergym.backendapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.synergym.backendapi.dto.*;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;

import java.io.IOException;

public interface AuthService {

    /**
     * 일반 회원가입 처리
     * @param signupRequest 회원가입 요청 정보
     * @param profileImage 프로필 이미지 (선택)
     */
    void signUp(SignupRequest signupRequest, MultipartFile profileImage) throws IOException;

    /**
     * 로그인 처리 및 JWT 토큰 발급
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 결과 응답 (성공 여부, 토큰 등 포함)
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 이메일 중복 여부 확인
     * @param email 확인할 이메일
     * @return true = 존재함, false = 사용 가능
     */
    boolean checkEmailExists(String email);

    /**
     * 이름과 생년월일로 이메일 찾기
     * @param findEmailRequest 이름 + 생년월일
     * @return 사용자 이메일
     */
    String findEmail(FindEmailRequest findEmailRequest);

    /**
     * 비밀번호 초기화 (임시 비밀번호 발송)
     * @param resetPasswordRequest 이메일 + 이름
     * @return 안내 메시지
     */
    String resetPassword(ResetPasswordRequest resetPasswordRequest);

    /**
     * 이메일 인증 코드 전송 (일반 가입자 전용)
     * @param email 대상 이메일
     */
    void sendVerificationCode(String email);

    /**
     * 이메일로 전송된 인증 코드 검증
     * @param email 사용자 이메일
     * @param code 사용자 입력 코드
     * @return true = 일치, false = 실패
     */
    boolean verifyCode(String email, String code);

    /**
     * 비밀번호 변경 처리
     * @param changePasswordRequest 비밀번호 변경 요청 정보
     */
    void changePassword(ChangePasswordRequest changePasswordRequest);

    /**
     * 소셜 회원가입 처리 및 로그인 처리
     * @param signupRequest 소셜 회원가입 요청 정보
     * @return 로그인 응답 (JWT 포함)
     */
    LoginResponse socialSignUp(SocialSignupRequest signupRequest);

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
                .provider("synergym")
                .build();
    }

    default UserDTO entityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .goal(user.getGoal())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .weight(user.getWeight())
                .height(user.getHeight())
                .role(user.getRole())
                .build();
    }
}
