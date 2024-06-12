package com.simongarton.platform.model;

import lombok.Getter;

@Getter
public enum APIStatusCode {

    OK(200),
    ACCEPTED(201),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    UNKNOWN_METHOD(405),
    CONFLICT(409),
    RATE_LIMIT_EXCEEDED(429),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;

    APIStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }

}
