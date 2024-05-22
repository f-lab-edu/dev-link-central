package dev.linkcentral.common.exception;

/**
 * 회원 이메일을 찾을 수 없을 경우 예외
 */
public class MemberEmailNotFoundException extends RuntimeException {

    public MemberEmailNotFoundException(String message) {
        super(message);
    }
}