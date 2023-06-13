package com.itonse.cms.user.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{  // 커스텀익셉션
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDetail());  // 오류 디테일을 RuntimeException 클래스의 생성자에 전달하여 오류 메시지로 설정
        this.errorCode = errorCode;
    }
}
