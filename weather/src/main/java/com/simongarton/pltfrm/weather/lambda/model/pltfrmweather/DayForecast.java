package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherDay;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
@Setter
public class DayForecast {

    private String timestamp;
    @Getter
    @Setter
    private String actualTime;
    @Getter
    @Setter
    private List<WeatherDay> forecastDays;

    @DynamoDbPartitionKey
    public String getTimestamp() {
        return this.timestamp;
    }
}
