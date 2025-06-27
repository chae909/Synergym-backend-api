package org.synergym.backendapi.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.synergym.backendapi.exception.ErrorCode;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;


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

    // 유효성 검사(Validation) 실패 시 사용할 응답
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
