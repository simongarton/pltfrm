package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherDay;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@DynamoDbBean
@Setter
public class DayForecast {

    private String timestamp;
    private int forecastDayIndex;
    @Getter
    @Setter
    private List<WeatherDay> forecastDays;

    @DynamoDbPartitionKey
    public String getTimestamp() {
        return this.timestamp;
    }

    @DynamoDbSortKey
    public int getForecastDayIndex() {
        return this.forecastDayIndex;
    }
}
