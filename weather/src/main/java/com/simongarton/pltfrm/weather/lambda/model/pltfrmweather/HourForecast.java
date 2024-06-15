package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherHour;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@DynamoDbBean
@Setter
public class HourForecast {

    private String timestamp;
    private int forecastHourIndex;
    @Getter
    @Setter
    private List<WeatherHour> forecastHours;

    @DynamoDbPartitionKey
    public String getTimestamp() {
        return this.timestamp;
    }

    @DynamoDbSortKey
    public int getForecastHourIndex() {
        return this.forecastHourIndex;
    }
}
