package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@DynamoDbBean
public class PltfrmWeatherLog {

    private String id;
    @Getter
    private String timestamp;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

}
