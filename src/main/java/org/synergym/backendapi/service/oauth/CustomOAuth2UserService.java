package org.synergym.backendapi.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.synergym.backendapi.entity.Role;
import org.synergym.backendapi.entity.User;
import org.synergym.backendapi.repository.UserRepository;

import java.util.Map;
import java.util.Optional;

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
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        // 우리 DB에 사용자가 있는지 확인
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // 이미 가입된 사용자인 경우
            user = userOptional.get();
            log.info("기존 사용자로 로그인: {}", email);
        } else {
            // 처음 소셜 로그인하는 사용자인 경우, 자동 회원가입
            user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode("SocialLoginPassword")) // 소셜 로그인 사용자는 비밀번호를 직접 사용하지 않음
                    .role(Role.MEMBER)
                    // provider, providerId 필드를 User 엔티티에 추가하는 것을 권장
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

