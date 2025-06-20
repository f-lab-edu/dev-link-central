package dev.common.exception;

import org.springframework.http.HttpStatus;

import javax.validation.Payload;

public interface CommonError extends Payload {

    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
