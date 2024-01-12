package dev.linkcentral.common.exception;

/**
 * 중복된 이메일 예외.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}