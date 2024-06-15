package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class WeatherTemp {

    private double day;
    private double min;
    private double max;
    private double night;
    private double eve;
    private double morn;

}
