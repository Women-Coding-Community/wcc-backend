package com.wcc.platform.domain.exceptions;

public class PlatformInternalException extends RuntimeException {
    public PlatformInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
