package org.synergym.backendapi.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.synergym.backendapi.exception.ErrorCode;

import java.time.LocalDateTime;

@Getter
@Builder

// 에러 응답 DTO
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now(); // 오류 발생 시각
    private final int status; // http 상태코드
    private final String error; // http 상태명칭
    private final String code; // 시스템  정의 에러 코드
    private final String message; // 에러 상세 메세지
    private final String path; // 요청한 api 경로

    /**
     * ErrorCode 객체를 기반으로 ResponseEntity 생성
     * (주로 커스텀 예외 처리 시 사용)
     *
     * @param errorCode 정의된 에러 코드
     * @param path 요청 경로
     * @return ResponseEntity<ErrorResponse>
     */
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String path) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .path(path)
                        .build()
                );
    }

    /**
     * 유효성 검사 실패 등 다양한 예외 케이스에 대해 직접 상태 코드와 메시지를 지정하여 응답
     *
     * @param status HTTP 상태 코드
     * @param error 상태 명칭
     * @param code 시스템 에러 코드
     * @param message 상세 메시지
     * @param path 요청 경로
     * @return ResponseEntity<ErrorResponse>
     */
    public static ResponseEntity<ErrorResponse> toResponseEntity(int status, String error, String code, String message, String path) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .status(status)
                        .error(error)
                        .code(code)
                        .message(message)
                        .path(path)
                        .build()
                );
    }
}
