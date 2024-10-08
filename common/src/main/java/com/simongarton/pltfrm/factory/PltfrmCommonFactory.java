package com.simongarton.pltfrm.factory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simongarton.pltfrm.service.*;

import java.time.format.DateTimeFormatter;

public class PltfrmCommonFactory {

    public static ObjectMapper getObjectMapper() {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }

    public static ObjectMapper getIgnoreUnknownsObjectMapper() {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // TODO controversial, needed for update
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ISO_DATE_TIME;
    }

    public static DateTimeFormatter getOffsetDateTimeFormatter() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    public static DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ISO_DATE;
    }

    public static LambdaRequestHandlerFactory getLambdaRequestHandlerFactory() {
        return new LambdaRequestHandlerFactory();
    }

    public static PltfrmSSMService getPltfrmSSMService() {
        return new PltfrmSSMService();
    }

    public static PltfrmS3Service getPltfrmS3Service() {
        return new PltfrmS3Service();
    }

    public static PltfrmSNSService getPltfrmSNSService() {
        return new PltfrmSNSService();
    }

    public static PltfrmDynamoDBService getPltfrmDynamoDBService() {
        return new PltfrmDynamoDBService();
    }

    public static PltfrmCloudwatchService getPltfrmCloudwatchService() {
        return new PltfrmCloudwatchService();
    }

    public static PltfrmEventBridgeService getPltfrmEventBridgeService() {
        return new PltfrmEventBridgeService();
    }
}
