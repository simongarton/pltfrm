package com.simongarton.platform.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class APIMessage {

    private int httpCode;
    private String message;
}
