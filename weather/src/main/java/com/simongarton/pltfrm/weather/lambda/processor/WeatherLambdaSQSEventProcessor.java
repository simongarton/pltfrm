package com.simongarton.pltfrm.weather.lambda.processor;


import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.service.PltfrmS3Service;
import com.simongarton.pltfrm.service.PltfrmSSMService;
import com.simongarton.pltfrm.service.PltfrmTimestreamService;
import com.simongarton.pltfrm.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.FileNotification;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.Weather;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherCurrentAndForecast;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherMinute;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import com.simongarton.pltfrm.weather.lambda.service.WeatherTimestreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WeatherLambdaSQSEventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmTimestreamService.class);

    private static final String WEATHER_BUCKET_NAME = "/pltfrm/weather-bucket-name";
    private static final String WEATHER_DATABASE_NAME = "/pltfrm/weather-timestream-database-name";

    private final PltfrmS3Service s3Service;
    private final WeatherDynamoDBService weatherDynamoDBService;
    private final WeatherTimestreamService weatherTimestreamService;

    private final String bucketName;
    private final String databaseName;
    private final String timestreamTableName;
    private final String dynamoWeatherTableName;
    private final String dynamoForecastHourTableName;
    private final String dynamoForecastDayTableName;

    public WeatherLambdaSQSEventProcessor(
            final PltfrmS3Service s3Service,
            final WeatherDynamoDBService weatherDynamoDBService,
            final WeatherTimestreamService weatherTimestreamService) {

        this.s3Service = s3Service;
        this.weatherDynamoDBService = weatherDynamoDBService;
        this.weatherTimestreamService = weatherTimestreamService;

        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        this.bucketName = pltfrmSSMService.getParameterValue(WEATHER_BUCKET_NAME);
        this.databaseName = pltfrmSSMService.getParameterValue(WEATHER_DATABASE_NAME);
        this.timestreamTableName = WeatherTimestreamService.PLATFORM_WEATHER_TABLE_NAME;
        this.dynamoWeatherTableName = WeatherDynamoDBService.PLATFORM_WEATHER_WEATHER_TABLE;
        this.dynamoForecastHourTableName = WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_HOUR_TABLE;
        this.dynamoForecastDayTableName = WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_DAY_TABLE;
    }

    public void processFile(final FileNotification fileNotification) throws IOException {

        final WeatherCurrentAndForecast weatherCurrentAndForecast =
                (WeatherCurrentAndForecast) this.s3Service.load(
                        fileNotification.getKey(),
                        this.bucketName,
                        WeatherCurrentAndForecast.class);
        this.weatherTimestreamService.saveData(weatherCurrentAndForecast, this.databaseName, this.timestreamTableName);

        this.handleWritesToDynamoDB(weatherCurrentAndForecast);
    }

    private void handleWritesToDynamoDB(final WeatherCurrentAndForecast weatherCurrentAndForecast) {
        final OffsetDateTime nowInPacificAuckland = DateTimeUtils.inPacificAuckland(OffsetDateTime.now());
        final String currentHour = DateTimeUtils.asOffsetDateTimeString(nowInPacificAuckland.truncatedTo(ChronoUnit.HOURS));
        final String currentDay = DateTimeUtils.asOffsetDateTimeString(nowInPacificAuckland.truncatedTo(ChronoUnit.DAYS));

        if (!this.updateDone(currentHour, this.dynamoWeatherTableName)) {
            this.writeCurrentWeatherToDynamoDB(weatherCurrentAndForecast, currentHour);
            LOG.info("Wrote current weather to DynamoDB");
        }
        if (!this.updateDone(currentHour, this.dynamoForecastHourTableName)) {
            this.writeForecastHourToDynamoDB(weatherCurrentAndForecast, currentHour);
            LOG.info("Wrote forecast hour to DynamoDB");
        }
        if (!this.updateDone(currentDay, this.dynamoForecastDayTableName)) {
            this.writeForecastDayToDynamoDB(weatherCurrentAndForecast, currentDay);
            LOG.info("Wrote forecast day to DynamoDB");
        }
    }

    private void writeForecastDayToDynamoDB(final WeatherCurrentAndForecast weatherCurrentAndForecast, final String currentDay) {

        final DayForecast dayForecast = new DayForecast();
        dayForecast.setTimestamp(currentDay);
        dayForecast.setActualTime(DateTimeUtils.asOffsetDateTimeString(
                DateTimeUtils.inPacificAuckland(OffsetDateTime.now())
        ));
        dayForecast.setForecastDays(weatherCurrentAndForecast.getDayList());

        this.weatherDynamoDBService.putDayForecast(dayForecast);

        this.doUpdate(currentDay, this.dynamoForecastDayTableName);
    }

    private void writeForecastHourToDynamoDB(final WeatherCurrentAndForecast weatherCurrentAndForecast, final String currentHour) {

        final HourForecast hourForecast = new HourForecast();
        hourForecast.setTimestamp(currentHour);
        hourForecast.setActualTime(DateTimeUtils.asOffsetDateTimeString(
                DateTimeUtils.inPacificAuckland(OffsetDateTime.now())
        ));
        hourForecast.setForecastHours(weatherCurrentAndForecast.getHourList());

        this.weatherDynamoDBService.putHourForecast(hourForecast);

        this.doUpdate(currentHour, this.dynamoForecastHourTableName);
    }

    private boolean updateDone(final String timestamp, final String key) {
        final String log = this.weatherDynamoDBService.getLog(key);
        if (log != null && log.equals(timestamp)) {
            LOG.info("Found same timestamp {} for {}", timestamp, key);
            return true;
        }
        if (log != null) {
            LOG.info("Found different timestamp {} for {}", log, key);
        } else {
            LOG.info("Found no timestamp or {}", key);
        }
        return false;
    }

    private void doUpdate(final String timestamp, final String key) {
        this.weatherDynamoDBService.setLog(key, timestamp);
        LOG.info("Updated timestamp {} for {}", timestamp, key);
    }

    private void writeCurrentWeatherToDynamoDB(final WeatherCurrentAndForecast weatherCurrentAndForecast, final String key) {

        final Map<String, String> values = new HashMap<>();
        for (final WeatherMinute weatherMinute : weatherCurrentAndForecast.getMinuteList()) {
            values.put(DateTimeUtils.asOffsetDateTimeString(
                            DateTimeUtils.longToOffsetDateTime(weatherMinute.getDt())),
                    String.format("%.2f", weatherMinute.getPrecipitation()));
        }

        final String weather = weatherCurrentAndForecast.getCurrent().getWeather().stream().map(Weather::getMain).collect(Collectors.joining(","));

        final Map<String, Object> item = new HashMap<>();
        // this is deliberately lowercase to match the enhanced dynamoDB mapper : which is opinionated and expects lowercase.
        // I could either rename the Java fields to be "Timestamp", or there is probably an annotation I could use.
        item.put("timestamp", key);
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
        this.weatherDynamoDBService.putItem(
                item,
                this.dynamoWeatherTableName)
        ;

        this.doUpdate(key, this.dynamoWeatherTableName);
    }
}
