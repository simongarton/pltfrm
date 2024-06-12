package com.simongarton.pltfrm.weather.lambda.model;

import lombok.Data;

@Data
public class WeatherTemp {

    private double day;
    private double min;
    private double max;
    private double night;
    private double eve;
    private double morn;

}
