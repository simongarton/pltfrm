package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class WeatherOverview {

    private double lat;
    private double lon;
    private String tz;
    private String date;
    private String units;
    @JsonProperty("weather_overview")
    private String weatherOverview;
}
