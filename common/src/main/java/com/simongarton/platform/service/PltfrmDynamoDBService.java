package com.simongarton.platform.service;

import com.simongarton.platform.factory.AWSFactory;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.format.DateTimeFormatter;

public class PltfrmDynamoDBService {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmDynamoDBService.class);

    private final DynamoDbClient dynamoDBClient;
    private final DateTimeFormatter dateTimeFormatter;

    public PltfrmDynamoDBService() {
        this.dynamoDBClient = AWSFactory.getDynamoDBClient();
        this.dateTimeFormatter = PltfrmCommonFactory.getDateTimeFormatter();
    }

}
