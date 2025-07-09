package org.synergym.backendapi.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security 관련 예외를 처리하는 글로벌 핸들러
 * 보안 예외를 JSON 형태로 클라이언트에 반환
 */
@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    /**
     * 인가(Authorization) 실패 처리 - 권한 부족
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException e) {
        String requestPath = getCurrentRequestPath();
        log.warn("접근 권한이 거부되었습니다. 경로: {}, 오류: {}", requestPath, e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "해당 리소스에 접근할 권한이 없습니다.",
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 인증(Authentication) 실패 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException e) {
        String requestPath = getCurrentRequestPath();
        log.warn("인증에 실패했습니다. 경로: {}, 오류: {}", requestPath, e.getMessage());

        String message = "인증이 필요합니다.";

        // 구체적인 인증 실패 유형별 메시지 처리
        if (e instanceof BadCredentialsException) {
            message = "잘못된 인증 정보입니다.";
        } else if (e instanceof InsufficientAuthenticationException) {
            message = "인증 토큰이 필요합니다.";
        }

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                message,
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * JWT 토큰 오류 처리
     * 유효하지 않거나 만료된 토큰 등
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(io.jsonwebtoken.JwtException e) {
        String requestPath = getCurrentRequestPath();
        log.warn("JWT 토큰 오류. 경로: {}, 오류: {}", requestPath, e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "유효하지 않은 토큰입니다. 다시 로그인해주세요.",
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 기타 Security 관련 예외 처리
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException e) {
        String requestPath = getCurrentRequestPath();
        log.error("보안 오류가 발생했습니다. 경로: {}, 오류: {}", requestPath, e.getMessage());

        Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Security Error",
                "보안 정책에 의해 요청이 거부되었습니다.",
                requestPath
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 공통 에러 응답 Map 생성
     * @param status HTTP 상태 코드
     * @param error 에러 유형 명칭
     * @param message 사용자에게 표시할 메시지
     * @param path 요청 경로
     */
    private Map<String, Object> createErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        errorResponse.put("success", false); // 클라이언트에서 공통 처리 시 사용

        return errorResponse;
    }

    /**
     * 현재 요청 URI 경로 추출
     * 예외 발생 시 로그 및 응답에 활용
     */
    private String getCurrentRequestPath() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            return request.getRequestURI();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
