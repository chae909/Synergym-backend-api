package org.synergym.backendapi.exception;

import lombok.Getter;

/**
 * 리소스를 찾을 수 없을 때 발생시키는 커스텀 예외 클래스
 * 예: 사용자, 게시글, 분석 결과 등이 DB에 존재하지 않을 경우
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    /** 에러 코드 (HttpStatus, 메시지 등 포함) */
    private final ErrorCode errorCode;

    /**
     * 생성자
     * @param errorCode ErrorCode Enum (예: USER_NOT_FOUND, DATA_NOT_EXIST 등)
     */
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException 메시지 설정
        this.errorCode = errorCode;
    }
}
