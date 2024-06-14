package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@DynamoDbBean
public class Log {

    private String weatherTable;
    private String timestamp;

    @DynamoDbPartitionKey
    public String getWeatherTable() {
        return this.weatherTable;
    }

    public void setWeatherTable(String weatherTable) {
        this.weatherTable = weatherTable;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
