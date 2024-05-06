package dev.linkcentral.common.exception;

/**
 * 중복된 닉네임 예외
 */
public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String message) {
        super(message);
    }
}



