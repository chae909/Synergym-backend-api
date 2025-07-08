package org.synergym.backendapi.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.synergym.backendapi.provider.JwtTokenProvider;
import org.synergym.backendapi.service.oauth.CustomOAuth2User;
import org.synergym.backendapi.util.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;
    private final String frontendUrl = "http://localhost:5173"; // React 앱 주소

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String targetUrl;

        if (oAuth2User.isNewUser()) {
            // [수정] GitHub 관련 분기문 제거 후 단순화
            log.info("새로운 소셜 사용자. 추가 정보 입력 페이지로 리디렉션합니다.");
            String email = oAuth2User.getEmail();
            String name = (String) oAuth2User.getAttributes().get("name");
            String provider = (String) oAuth2User.getAttributes().get("provider");

            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/social-signup")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("provider", provider)
                    .build().toUriString();
        } else {
            // 기존 사용자: JWT 발급 후 리디렉션
            log.info("기존 소셜 사용자. JWT 발급 및 리디렉션합니다.");
            String token = jwtTokenProvider.generateToken(authentication);
            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth/redirect")
                    .queryParam("token", token)
                    .build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}