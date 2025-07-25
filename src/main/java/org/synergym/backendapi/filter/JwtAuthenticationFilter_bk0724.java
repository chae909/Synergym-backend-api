// package org.synergym.backendapi.filter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.NonNull;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.util.StringUtils;
// import org.springframework.web.filter.OncePerRequestFilter;
// import org.synergym.backendapi.provider.JwtTokenProvider;

// import java.io.IOException;

// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     private final JwtTokenProvider jwtTokenProvider;

//     @Override
//     protected void doFilterInternal(
//             @NonNull HttpServletRequest request,
//             @NonNull HttpServletResponse response,
//             @NonNull FilterChain filterChain) throws ServletException, IOException {

//         try {
//             // 1. 요청 헤더에서 토큰 추출
//             String token = resolveToken(request);

//             // 2. 토큰 유효성 검사
//             if (token != null && jwtTokenProvider.validateToken(token)) {
//                 // 토큰이 유효할 경우, 토큰에서 인증 정보(Authentication)를 가져와 SecurityContext에 저장
//                 Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             }
//         } catch (Exception e) {
//             // 예외 발생 시 SecurityContext를 비워 보안 위험을 방지
//             SecurityContextHolder.clearContext();
//             logger.error("Could not set user authentication in security context", e);
//             // 필요에 따라 response.sendError() 등을 사용하여 클라이언트에 에러 응답을 보낼 수 있습니다.
//         }


//         // 다음 필터 체인 실행
//         filterChain.doFilter(request, response);
//     }

//     /**
//      * HttpServletRequest의 헤더에서 'Authorization'을 찾아 Bearer 토큰을 추출합니다.
//      * @param request HttpServletRequest 객체
//      * @return 추출된 토큰 문자열 (없거나 형식이 맞지 않으면 null)
//      */
//     private String resolveToken(HttpServletRequest request) {
//         String bearerToken = request.getHeader("Authorization");
//         // 'Authorization' 헤더가 존재하고 'Bearer '로 시작하는지 확인
//         if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//             // 'Bearer ' 부분을 제외한 순수 토큰 값만 반환
//             return bearerToken.substring(7);
//         }
//         return null;
//     }
// }