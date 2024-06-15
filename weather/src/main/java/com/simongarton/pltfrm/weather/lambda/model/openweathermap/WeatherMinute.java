package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class WeatherMinute {

    private long dt;
    private double precipitation;
}
