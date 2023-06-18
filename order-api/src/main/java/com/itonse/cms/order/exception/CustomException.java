package com.itonse.cms.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private final int status;
    private static final ObjectMapper mapper = new ObjectMapper();

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    public static class CustomExceptionResponse{  // 예외에 대한 응답 모델 (사용자에게 보이는)
        private int status;
        private String code;
        private String message;
    }
}
