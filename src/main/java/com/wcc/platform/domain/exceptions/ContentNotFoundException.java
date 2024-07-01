package com.wcc.platform.domain.exceptions;

public class ContentNotFoundException extends RuntimeException {

    public ContentNotFoundException(String message) {
        super(message);
    }
}
