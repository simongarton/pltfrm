package com.simongarton.pltfrm.exception;

import java.io.Serial;

public class NotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5790604815827269131L;

    public NotFoundException(final String message) {
        super(message);
    }
}
