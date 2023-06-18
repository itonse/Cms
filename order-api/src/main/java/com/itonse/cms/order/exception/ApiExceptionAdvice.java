package com.itonse.cms.order.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(
            final CustomException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(CustomException.CustomExceptionResponse.builder()
                        .message(exception.getMessage())
                        .code(exception.getErrorCode().name())  // .name 은 에러네임(ex SAME_ITEM_NAME)
                        .status(exception.getStatus())
                        .build());
    }
}
