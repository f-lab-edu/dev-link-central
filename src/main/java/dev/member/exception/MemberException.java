package dev.member.exception;

import dev.common.exception.CommonError;
import dev.common.exception.ServiceException;
import lombok.Getter;

@Getter
public class MemberException extends ServiceException {

    public MemberException(CommonError commonError) {
        super(commonError);
    }
}
