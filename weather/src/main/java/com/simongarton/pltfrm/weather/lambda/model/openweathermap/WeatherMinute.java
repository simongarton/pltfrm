package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import lombok.Data;

@Data
public class WeatherMinute {

    private long dt;
    private double precipitation;
}
