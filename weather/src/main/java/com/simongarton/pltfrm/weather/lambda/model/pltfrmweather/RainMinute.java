package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RainMinute {

    private OffsetDateTime timestamp;
    private double precipitation;
}
