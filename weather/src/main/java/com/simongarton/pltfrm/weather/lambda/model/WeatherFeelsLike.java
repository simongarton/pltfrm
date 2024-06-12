package com.simongarton.pltfrm.weather.lambda.model;

import lombok.Data;

@Data
public class WeatherFeelsLike {

    private double day;
    private double night;
    private double eve;
    private double morn;
}
