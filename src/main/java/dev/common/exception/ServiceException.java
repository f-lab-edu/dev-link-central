package dev.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{

    private final CommonError commonError;

    public ServiceException(CommonError commonError) {
        super(commonError.getMessage());
        this.commonError = commonError;
    }
}
