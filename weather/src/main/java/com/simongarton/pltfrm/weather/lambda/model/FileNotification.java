package com.simongarton.pltfrm.weather.lambda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNotification {

    private String bucket;
    private String key;
}
