package com.simongarton.pltfrm.weather.lambda.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileNotification {

    private String bucket;
    private String key;
}
