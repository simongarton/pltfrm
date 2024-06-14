package com.simongarton.pltfrm.weather.lambda.processor;


import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.service.PltfrmDynamoDBService;
import com.simongarton.platform.service.PltfrmS3Service;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.platform.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.FileNotification;
import com.simongarton.pltfrm.weather.lambda.model.Weather;
import com.simongarton.pltfrm.weather.lambda.model.WeatherCurrentAndForecast;
import com.simongarton.pltfrm.weather.lambda.model.WeatherMinute;
import com.simongarton.pltfrm.weather.lambda.service.WeatherTimestreamService;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherLambdaSQSEventProcessor {

    private static final String WEATHER_BUCKET_NAME = "/pltfrm/weather-bucket-name";
    private static final String WEATHER_DATABASE_NAME = "/pltfrm/weather-timestream-database-name";

    private final PltfrmS3Service s3Service;
    private final PltfrmDynamoDBService pltfrmDynamoDBService;
    private final WeatherTimestreamService weatherTimestreamService;

    private final String bucketName;
    private final String databaseName;
    private final String timestreamTableName;
    private final String dynamoRainTableName;

    public WeatherLambdaSQSEventProcessor(
            final PltfrmS3Service s3Service,
            final PltfrmDynamoDBService pltfrmDynamoDBService,
            final WeatherTimestreamService weatherTimestreamService) {
        this.s3Service = s3Service;
        this.pltfrmDynamoDBService = pltfrmDynamoDBService;
        this.weatherTimestreamService = weatherTimestreamService;

        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        this.bucketName = pltfrmSSMService.getParameterValue(WEATHER_BUCKET_NAME);
        this.databaseName = pltfrmSSMService.getParameterValue(WEATHER_DATABASE_NAME);
        this.timestreamTableName = "pltfrm_weather_day_table";
        this.dynamoRainTableName = "pltfrm_weather_rain_table";
    }


    public void processFile(final FileNotification fileNotification) throws IOException {

        final WeatherCurrentAndForecast weatherCurrentAndForecast =
                (WeatherCurrentAndForecast) this.s3Service.load(
                        fileNotification.getKey(),
                        this.bucketName,
                        WeatherCurrentAndForecast.class);
        this.weatherTimestreamService.saveData(weatherCurrentAndForecast, this.databaseName, this.timestreamTableName);

        this.writeCurrentWeatherToDynamoDB(weatherCurrentAndForecast);
    }

    private void writeCurrentWeatherToDynamoDB(final WeatherCurrentAndForecast weatherCurrentAndForecast) {

        final String key = DateTimeUtils.asOffsetDateTimeString(OffsetDateTime.now());
        final Map<String, String> values = new HashMap<>();
        for (final WeatherMinute weatherMinute : weatherCurrentAndForecast.getMinuteList()) {
            values.put(DateTimeUtils.asOffsetDateTimeString(
                            DateTimeUtils.longToOffsetDateTime(weatherMinute.getDt())),
                    String.format("%.2f", weatherMinute.getPrecipitation()));
        }

        final String weather = weatherCurrentAndForecast.getCurrent().getWeather().stream().map(Weather::getMain).collect(Collectors.joining(","));

        final Map<String, Object> item = new HashMap<>();
        item.put("Timestamp", key);
        item.put("Temperature", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getTemp()));
        item.put("FeelsLike", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getFeelsLike()));
        item.put("Pressure", String.format("%d", weatherCurrentAndForecast.getCurrent().getPressure()));
        item.put("Humidity", String.format("%d", weatherCurrentAndForecast.getCurrent().getHumidity()));
        item.put("DewPoint", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getDewPoint()));
        item.put("Uvi", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getUvi()));
        item.put("Clouds", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getClouds()));
        item.put("Visibility", String.format("%d", weatherCurrentAndForecast.getCurrent().getVisibility()));
        item.put("WindSpeed", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getWindSpeed()));
        item.put("WindDeg", String.format("%d", weatherCurrentAndForecast.getCurrent().getWindDeg()));
        item.put("WindGust", String.format("%.2f", weatherCurrentAndForecast.getCurrent().getWindGust()));
        item.put("Weather", weather);
        item.put("Rain", values);
        this.pltfrmDynamoDBService.putItem(
                item,
                this.dynamoRainTableName);
    }
}
