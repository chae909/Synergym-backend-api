package org.synergym.backendapi.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.synergym.backendapi.dto.ErrorResponse;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;

/**
 * 전역 예외 처리 핸들러
 * 모든 Controller에서 발생하는 예외를 공통으로 처리하여 일관된 에러 응답을 반환
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 데이터가 존재하지 않을 때 발생하는 예외 처리
     * 예: 특정 ID의 사용자를 찾지 못했을 때
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        return ResponseEntity.notFound().build(); // 404 응답, 바디는 없음
    }

    /**
     * @Valid 검증 실패 시 처리
     * 예: DTO의 필드 제약 조건 위반 등
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        // 첫 번째 에러 메시지 추출 (여러 필드 중 하나만 보여줌)
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("MethodArgumentNotValidException: {}", errorMessage);

        return ErrorResponse.toResponseEntity(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                ErrorCode.INVALID_INPUT_VALUE.name(),  // 사전에 정의된 커스텀 에러 코드
                errorMessage,
                request.getDescription(false).replace("uri=", "") // 요청 경로 추출
        );
    }

    /**
     * IllegalStateException 처리
     * 간단한 상태 오류 (예: 조건 불충족 등) 시 사용
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 처리되지 않은 모든 예외 처리 (최종 fallback)
     * 서버 오류 등 예상치 못한 에러에 대응
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unhandled Exception: ", ex);

        return ErrorResponse.toResponseEntity(
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getDescription(false).replace("uri=", "")
        );
    }
}
