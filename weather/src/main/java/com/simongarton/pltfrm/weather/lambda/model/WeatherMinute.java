package com.simongarton.pltfrm.weather.lambda.model;

import lombok.Data;

@Data
public class WeatherMinute {

    private long dt;
    private double precipitation;
}
