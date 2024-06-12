package com.simongarton.pltfrm.weather.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherCurrent {

    private long dt;
    private long sunrise;
    private long sunset;
    private double temp;
    @JsonProperty("feels_like")
    private double feelsLike;
    private int pressure;
    private int humidity;
    @JsonProperty("dew_point")
    private double dewPoint;
    private double uvi;
    private double clouds;
    private int visibility;
    @JsonProperty("wind_speed")
    private double windSpeed;
    @JsonProperty("wind_deg")
    private int windDeg;
    @JsonProperty("wind_gust")
    private double windGust;
    private List<Weather> weather;
}
