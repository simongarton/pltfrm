package com.simongarton.pltfrm.weather.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherDay {

    private long dt;
    private long sunrise;
    private long sunset;
    private long moonrise;
    private long moonset;
    private double moon_phase;
    private String summary;
    private WeatherTemp temp;
    @JsonProperty("feels_like")
    private WeatherFeelsLike feelsLike;
    private int pressure;
    private int humidity;
    @JsonProperty("dew_point")
    private double dewPoint;
    @JsonProperty("wind_speed")
    private double windSpeed;
    @JsonProperty("wind_deg")
    private int windDeg;
    @JsonProperty("wind_gust")
    private double windGust;
    private List<Weather> weather;
    private double clouds;
    private double pop;
    private double rain;
    private double uvi;
}
