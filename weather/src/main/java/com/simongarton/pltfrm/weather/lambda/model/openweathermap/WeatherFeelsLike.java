package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class WeatherFeelsLike {

    private double day;
    private double night;
    private double eve;
    private double morn;
}
