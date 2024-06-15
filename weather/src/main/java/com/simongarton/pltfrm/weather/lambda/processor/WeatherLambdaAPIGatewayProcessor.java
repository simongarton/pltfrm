package com.simongarton.pltfrm.weather.lambda.processor;


import com.simongarton.platform.exception.InternalServerErrorException;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class WeatherLambdaAPIGatewayProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaAPIGatewayProcessor.class);

    private final OpenWeatherMapClient openWeatherMapClient;
    private final WeatherDynamoDBService weatherDynamoDBService;

    public WeatherLambdaAPIGatewayProcessor(
            final OpenWeatherMapClient openWeatherMapClient,
            final WeatherDynamoDBService weatherDynamoDBService) {
        this.openWeatherMapClient = openWeatherMapClient;
        this.weatherDynamoDBService = weatherDynamoDBService;
    }

    public String getWeatherSummary() {

        try {
            return this.openWeatherMapClient.getWeatherSummaryFromAPI();
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to get weather : " + e.getMessage());
        }
    }

    public HourForecast getWeatherHourForecast() {

        // check the log table for the latest day forecast timestamp
        final String timestamp = this.weatherDynamoDBService.getLog(
                WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_HOUR_TABLE);
        LOG.info("for getWeatherHourForecast() I got a timestamp of {} on {}.",
                timestamp,
                WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_HOUR_TABLE);
        if (timestamp == null) {
            return null;
        }

        // now get the record
        final HourForecast hourForecast = this.weatherDynamoDBService.getHourForecast(timestamp);
        return hourForecast;
    }

    public DayForecast getWeatherDayForecast() {

        // check the log table for the latest day forecast timestamp
        final String timestamp = this.weatherDynamoDBService.getLog(
                WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_DAY_TABLE);
        LOG.info("for getWeatherHourForecast() I got a timestamp of {} on {}.",
                timestamp,
                WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_DAY_TABLE);
        if (timestamp == null) {
            return null;
        }

        // now get the record
        final DayForecast dayForecast = this.weatherDynamoDBService.getDayForecast(timestamp);
        return dayForecast;
    }
}
