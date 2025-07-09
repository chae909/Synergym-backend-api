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

/**
 * OAuth2 인증 성공 후 처리 핸들러
 * 신규 유저는 회원가입 추가 정보 페이지로,
 * 기존 유저는 JWT 토큰을 발급받아 프론트엔드로 리디렉션한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtUtil jwtUtil;

    /** 프론트엔드 애플리케이션 주소 */
    private final String frontendUrl = "http://localhost:5173"; // React 앱 주소

    /**
     * OAuth2 로그인 성공 시 호출되는 메서드
     *
     * @param request       HttpServletRequest
     * @param response      HttpServletResponse
     * @param authentication 인증 객체 (CustomOAuth2User 포함)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2 사용자 정보 추출
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String targetUrl;

        if (oAuth2User.isNewUser()) {
            // 신규 소셜 사용자일 경우: 추가 정보 입력 페이지로 리디렉션
            log.info("새로운 소셜 사용자. 추가 정보 입력 페이지로 리디렉션합니다.");
            String email = oAuth2User.getEmail();
            String name = (String) oAuth2User.getAttributes().get("name");
            String provider = (String) oAuth2User.getAttributes().get("provider");

            // 프론트엔드의 소셜 회원가입 화면으로 이동 (쿼리 파라미터 포함)
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/social-signup")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("provider", provider)
                    .build().toUriString();
        } else {
            // 기존 사용자일 경우: JWT 토큰 발급 후 리디렉션
            log.info("기존 소셜 사용자. JWT 발급 및 리디렉션합니다.");
            String token = jwtTokenProvider.generateToken(authentication);

            // 프론트엔드의 리디렉션 처리 경로로 이동 (JWT 포함)
            targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth/redirect")
                    .queryParam("token", token)
                    .build().toUriString();
        }

        // 클라이언트로 리디렉션 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
