package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@DynamoDbBean
public class PltfrmWeatherLog {

    @Setter
    private String id;
    @Getter
    @Setter
    private String timestamp;
    @Getter
    @Setter
    private String actualTime;

    @DynamoDbPartitionKey
    public String getId() {
        return this.id;
    }

}
