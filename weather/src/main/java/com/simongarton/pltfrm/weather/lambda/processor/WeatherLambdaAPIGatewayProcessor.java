package com.simongarton.pltfrm.weather.lambda.processor;


import com.simongarton.platform.exception.InternalServerErrorException;
import com.simongarton.platform.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherCurrentAndForecast;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherMinute;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.RainHourForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.RainMinute;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        LOG.info("for getWeatherDayForecast() I got a timestamp of {} on {}.",
                timestamp,
                WeatherDynamoDBService.PLATFORM_WEATHER_FORECAST_DAY_TABLE);
        if (timestamp == null) {
            return null;
        }

        // now get the record
        final DayForecast dayForecast = this.weatherDynamoDBService.getDayForecast(timestamp);
        return dayForecast;
    }

    public RainHourForecast getRainForecast() throws URISyntaxException, IOException, InterruptedException {

        // I am not storing the minute by minute rain forecast in DynamoDB yet.
        // I do have the files in S3 ...

        final WeatherCurrentAndForecast weatherCurrentAndForecast = this.openWeatherMapClient.getWeatherDetailsFromAPI();
        final RainHourForecast rainHourForecast = new RainHourForecast();
        final List<RainMinute> rainMinuteList = new ArrayList<>();
        for (final WeatherMinute weatherMinute : weatherCurrentAndForecast.getMinuteList()) {
            final RainMinute rainMinute = new RainMinute();
            rainMinute.setTimestamp(DateTimeUtils.inPacificAuckland(
                            DateTimeUtils.longToOffsetDateTime(
                                    weatherMinute.getDt()
                            )
                    )
            );
            rainMinute.setPrecipitation(weatherMinute.getPrecipitation());
            rainMinuteList.add(rainMinute);
        }
        rainHourForecast.setRainMinutes(rainMinuteList);

        return rainHourForecast;
    }
}
