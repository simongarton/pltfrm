package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

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
    // minutes will literally be the next 60 minutes, starting from now - so need to get just before the hour ?
    // or on the hour, starting at 1 minute past the hour, and going to 60 minutes past the hour.
    @JsonProperty("minutely")
    private List<WeatherMinute> minuteList;
    // this includes the current hour.
    @JsonProperty("hourly")
    private List<WeatherHour> hourList;
    // this is giving me midday for the current day : UTC midnight on the next day ?
    // it's currently 2024-06-14T04:58:44+12:00 and it's giving me 2024-06-14T12:00+12:00.
    @JsonProperty("daily")
    private List<WeatherDay> dayList;
    @JsonProperty("alerts")
    private List<WeatherAlert> alertList;

}
