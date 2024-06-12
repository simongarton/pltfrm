package com.simongarton.platform.exception;

public class InternalServerErrorException extends RuntimeException {

    private static final long serialVersionUID = -8566069070427978521L;

    public InternalServerErrorException(final String message) {
        super(message);
    }
}
