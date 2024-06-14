package com.simongarton.pltfrm.weather.lambda.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.service.PltfrmS3Service;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.model.FileNotification;
import com.simongarton.pltfrm.weather.lambda.model.WeatherCurrentAndForecast;
import com.simongarton.pltfrm.weather.lambda.service.WeatherTimestreamService;

import java.io.IOException;

public class WeatherLambdaSQSEventProcessor {

    private static final String WEATHER_BUCKET_NAME = "/pltfrm/weather-bucket-name";
    private static final String WEATHER_DATABASE_NAME = "/pltfrm/weather-timestream-database-name";

    private final ObjectMapper objectMapper;
    private final PltfrmS3Service s3Service;
    private final WeatherTimestreamService weatherTimestreamService;

    private final String bucketName;
    private final String databaseName;
    private final String tableName;

    public WeatherLambdaSQSEventProcessor(
            final PltfrmS3Service s3Service,
            final WeatherTimestreamService weatherTimestreamService) {
        this.s3Service = s3Service;
        this.weatherTimestreamService = weatherTimestreamService;
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();

        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        this.bucketName = pltfrmSSMService.getParameterValue(WEATHER_BUCKET_NAME);
        this.databaseName = pltfrmSSMService.getParameterValue(WEATHER_DATABASE_NAME);
        this.tableName = "pltfrm_weather_day_table";
    }


    public void processFile(final FileNotification fileNotification) throws IOException {

        final WeatherCurrentAndForecast weatherCurrentAndForecast =
                (WeatherCurrentAndForecast) this.s3Service.load(
                        fileNotification.getKey(),
                        this.bucketName,
                        WeatherCurrentAndForecast.class);
        this.weatherTimestreamService.saveData(weatherCurrentAndForecast, this.databaseName, this.tableName);
    }
}
