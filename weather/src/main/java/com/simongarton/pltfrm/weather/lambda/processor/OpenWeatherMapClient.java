package com.simongarton.pltfrm.weather.lambda.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.pltfrm.exception.InternalServerErrorException;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class OpenWeatherMapClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenWeatherMapClient.class);

    private static final String OVERVIEW_PATH = "/overview?lat=-36.848461&lon=174.763336&units=metric";
    private static final String DETAILS_PATH = "?lat=-36.848461&lon=174.763336&units=metric";

    private static final String X_API_KEY = "x-api-key";

    private final String url;
    private final String apiKey;

    private final ObjectMapper objectMapper;

    public OpenWeatherMapClient(final String url,
                                final String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    private HttpRequest buildRequest(final String path) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(this.url + path))
                .header(X_API_KEY, this.apiKey)
                .GET()
                .build();
    }

    public String getWeatherSummaryFromAPI() throws URISyntaxException, IOException, InterruptedException {

        final HttpRequest request = this.buildRequest(OVERVIEW_PATH);

        LOG.info("Request : {}", request.toString());

        final HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());


        LOG.info("Response : {}", response.body());
        final WeatherOverview weatherOverview = this.objectMapper.readValue(response.body(), WeatherOverview.class);
        return weatherOverview.getWeatherOverview();
    }

    public WeatherCurrentAndForecast getWeatherDetailsFromAPI() throws URISyntaxException, IOException, InterruptedException {

        final HttpRequest request = this.buildRequest(DETAILS_PATH);

        LOG.info("Request : {}", request.toString());

        final HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());


        LOG.info("Response : {}", response.body());
        return this.objectMapper.readValue(response.body(), WeatherCurrentAndForecast.class);
    }

    public void debugWeatherDates() {
        try {
            final WeatherCurrentAndForecast weatherCurrentAndForecast = this.getWeatherDetailsFromAPI();
            LOG.info("Current time : {}", OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS));
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
}
