package com.ivnrdev.connectodo.Exception;

import com.ivnrdev.connectodo.Response.BaseResponse;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.fasterxml.jackson.databind.util.ClassUtil.getRootCause;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleExceptionHandler(Exception ex) {
        Throwable rootCause = getRootCause(ex);
        rootCause.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(rootCause.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotFoundException(Exception ex) {
        Throwable rootCause = getRootCause(ex);
        rootCause.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(rootCause.getMessage()));
    }

    @ExceptionHandler({DbActionExecutionException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<BaseResponse<Void>> handleBadRequestExceptions(Exception ex) {
        Throwable rootCause = getRootCause(ex);
        rootCause.printStackTrace();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(rootCause.getMessage()));
    }

}
