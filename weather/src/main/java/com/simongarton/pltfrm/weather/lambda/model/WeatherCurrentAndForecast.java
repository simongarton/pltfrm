package com.simongarton.pltfrm.weather.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherCurrentAndForecast {

    private double lat;
    private double lon;
    private String timezone;
    @JsonProperty("timezone_offset")
    private int timezoneOffset;
    private WeatherCurrent current;
    @JsonProperty("minutely")
    private List<WeatherMinute> minuteList;
    @JsonProperty("hourly")
    private List<WeatherHour> hourList;
    @JsonProperty("daily")
    private List<WeatherDay> dayList;
    @JsonProperty("alerts")
    private List<WeatherAlert> alertList;

}
