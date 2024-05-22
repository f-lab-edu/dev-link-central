package dev.linkcentral.common.exception;

/**
 * 회원 가입 예외
 */
public class MemberRegistrationException extends RuntimeException {

    public MemberRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}