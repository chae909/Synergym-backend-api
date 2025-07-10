package org.synergym.backendapi.service.oauth;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("소셜 로그인 유저 정보: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        String provider = oAuth2UserInfo.getProvider();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        // 탈퇴한 사용자 포함하여 이메일로 사용자 확인
        Optional<User> userOptional = userRepository.findByEmailIncludingDeleted(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            
            // 이미 활성화된 사용자인 경우
            if (user.getUseYn() == 'Y') {
                log.info("기존 사용자로 로그인: {}", email);
            } else if (user.getUseYn() == 'N') {
                // 탈퇴한 사용자가 소셜 로그인으로 재가입하는 경우
                log.info("탈퇴한 사용자가 소셜 로그인으로 재가입합니다: {}", email);
                
                user.reactivate(); // useYn을 Y로 변경
                user.updateName(name);
                user.updateProvider(provider);
                user.updatePassword(passwordEncoder.encode("SocialLoginPassword"));
                
                userRepository.save(user);
                log.info("소셜 로그인 재가입 완료: {}", email);
            }
        } else {
            // 처음 소셜 로그인하는 사용자인 경우, 자동 회원가입
            user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode("SocialLoginPassword")) // 소셜 로그인 사용자는 비밀번호를 직접 사용하지 않음
                    .role(Role.MEMBER)
                    .provider(provider)
                    .build();
            userRepository.save(user);
            log.info("새로운 소셜 사용자로 회원가입: {}", email);
        }

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("naver")) {
            return new NaverOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("kakao")) {
            return new KakaoOAuth2UserInfo(attributes);
        }
        throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
    }
}

