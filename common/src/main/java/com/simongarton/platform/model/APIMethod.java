package com.simongarton.platform.model;

import lombok.Getter;

public enum APIMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    @Getter
    private final String method;

    APIMethod(final String method) {
        this.method = method;
    }
}
