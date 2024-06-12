package com.simongarton.platform.model;

import lombok.Getter;

@Getter
public enum APIMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    private final String method;

    APIMethod(final String method) {
        this.method = method;
    }
}
