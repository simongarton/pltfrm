package com.simongarton.pltfrm.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -7074461100702418092L;

    public BadRequestException(final String message) {
        super(message);
    }
}
