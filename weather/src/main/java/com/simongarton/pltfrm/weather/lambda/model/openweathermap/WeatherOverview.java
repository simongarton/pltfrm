package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherOverview {

    private double lat;
    private double lon;
    private String tz;
    private String date;
    private String units;
    @JsonProperty("weather_overview")
    private String weatherOverview;
}
