package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.service.PltfrmS3Service;
import com.simongarton.pltfrm.weather.lambda.factory.WeatherServiceFactory;
import com.simongarton.pltfrm.weather.lambda.model.FileNotification;
import com.simongarton.pltfrm.weather.lambda.model.SQSMessageBody;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaSQSEventProcessor;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import com.simongarton.pltfrm.weather.lambda.service.WeatherTimestreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WeatherLambdaSQSEventRequestHandler implements RequestHandler<SQSEvent, String> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaSQSEventRequestHandler.class);

    final private WeatherLambdaSQSEventProcessor processor;
    final private ObjectMapper objectMapper;

    public WeatherLambdaSQSEventRequestHandler() {
        final PltfrmS3Service pltfrmS3Service = PltfrmCommonFactory.getPltfrmS3Service();
        final WeatherDynamoDBService weatherDynamoDBService = WeatherServiceFactory.getWeatherDynamoDBService();
        final WeatherTimestreamService weatherTimestreamService = WeatherServiceFactory.getWeatherTimestreamService();
        this.processor = new WeatherLambdaSQSEventProcessor(pltfrmS3Service, weatherDynamoDBService, weatherTimestreamService);
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public String handleRequest(final SQSEvent sqsEvent,
                                final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        try {
            for (final SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
                final SQSMessageBody sqsMessageBody = this.objectMapper.readValue(message.getBody(), SQSMessageBody.class);
                final FileNotification fileNotification = this.objectMapper.readValue(sqsMessageBody.getMessage(), FileNotification.class);
                this.processor.processFile(fileNotification);
            }
            return "Processed " + sqsEvent.getRecords().size();

        } catch (final Exception e) {

            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}