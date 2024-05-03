package dev.linkcentral.common.exception;

public class ProfileUpdateException extends RuntimeException {
    public ProfileUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}