package com.simongarton.pltfrm.weather.lambda.model.openweathermap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Data
@DynamoDbBean
public class WeatherAlert {

    @JsonProperty("sender_name")
    private String senderName;
    private String event;
    private long start;
    private long end;
    private String description;
    private List<String> tags;
}
