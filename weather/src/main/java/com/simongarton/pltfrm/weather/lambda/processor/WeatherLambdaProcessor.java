package com.simongarton.pltfrm.weather.lambda.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.exception.InternalServerErrorException;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.service.PltfrmS3Service;
import com.simongarton.platform.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.WeatherLambdaAPIGatewayRequestHandler;
import com.simongarton.pltfrm.weather.lambda.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherLambdaProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaAPIGatewayRequestHandler.class);

    private static final String X_API_KEY = "x-api-key";
    private static final String BUCKET_NAME = "pltfrm-weather-file";

    private static final String OVERVIEW_PATH = "/overview?lat=-36.848461&lon=174.763336";
    private static final String DETAILS_PATH = "?lat=-36.848461&lon=174.763336";

    private final String url;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final PltfrmS3Service s3Service;

    public WeatherLambdaProcessor(final String url,
                                  final String apiKey,
                                  final PltfrmS3Service s3Service) {
        this.url = url;
        this.apiKey = apiKey;
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
        this.s3Service = s3Service;
    }

    public String getWeatherSummary() {

        try {
            return this.getWeatherSummaryFromAPI();
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to get weather : " + e.getMessage());
        }
    }

    public String getWeatherDetails() {

        try {
            return this.objectMapper.writeValueAsString(this.getWeatherDetailsFromAPI());
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to get weather : " + e.getMessage());
        }
    }

    public void debugWeatherDates() {
        try {
            final WeatherCurrentAndForecast weatherCurrentAndForecast = this.getWeatherDetailsFromAPI();
            LOG.info("Current time : {}", DateTimeUtils.offsetDateTimeNowToSeconds());
            final WeatherCurrent weatherCurrent = weatherCurrentAndForecast.getCurrent();
            LOG.info("Current Weather date : {}", DateTimeUtils.longToOffsetDateTime(weatherCurrent.getDt()));
            LOG.info("Minute Weather dates :");
            for (final WeatherMinute weatherMinute : weatherCurrentAndForecast.getMinuteList()) {
                LOG.info("\t{}", DateTimeUtils.longToOffsetDateTime(weatherMinute.getDt()));
            }
            LOG.info("Hour Weather dates :");
            for (final WeatherHour weatherHour : weatherCurrentAndForecast.getHourList()) {
                LOG.info("\t{}", DateTimeUtils.longToOffsetDateTime(weatherHour.getDt()));
            }
            LOG.info("Day Weather dates :");
            for (final WeatherDay weatherDay : weatherCurrentAndForecast.getDayList()) {
                LOG.info("\t{}", DateTimeUtils.longToOffsetDateTime(weatherDay.getDt()));
            }
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to get weather : " + e.getMessage());
        }
    }


    private String getWeatherSummaryFromAPI() throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.url + OVERVIEW_PATH))
                .header(X_API_KEY, this.apiKey)
                .GET()
                .build();

        LOG.info("Request : {}", request.toString());

        final HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());


        LOG.info("Response : {}", response.body());
        final WeatherOverview weatherOverview = this.objectMapper.readValue(response.body(), WeatherOverview.class);
        return weatherOverview.getWeatherOverview();
    }

    private WeatherCurrentAndForecast getWeatherDetailsFromAPI() throws URISyntaxException, IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.url + DETAILS_PATH))
                .header(X_API_KEY, this.apiKey)
                .GET()
                .build();

        LOG.info("Request : {}", request.toString());

        final HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());


        LOG.info("Response : {}", response.body());
        return this.objectMapper.readValue(response.body(), WeatherCurrentAndForecast.class);
    }

    public void downloadWeatherData() throws URISyntaxException, IOException, InterruptedException {

        final String filename = String.format("weather/%s.json", DateTimeUtils.nowAsFileNameISOinUTC());
        final WeatherCurrentAndForecast weatherCurrentAndForecast = this.getWeatherDetailsFromAPI();
        this.s3Service.save(weatherCurrentAndForecast,
                filename,
                BUCKET_NAME);
    }
}
