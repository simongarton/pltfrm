package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@DynamoDbBean
public class Weather {

    private int id;
    private String main;
    private String description;
    private String icon;
}
