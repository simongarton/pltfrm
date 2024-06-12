package com.simongarton.pltfrm.weather.lambda.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.exception.InternalServerErrorException;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.weather.lambda.WeatherLambdaRequestHandler;
import com.simongarton.pltfrm.weather.lambda.model.WeatherCurrentAndForecast;
import com.simongarton.pltfrm.weather.lambda.model.WeatherOverview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherLambdaProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaRequestHandler.class);

    private static final String X_API_KEY = "x-api-key";

    private static final String OVERVIEW_PATH = "/overview?lat=-36.848461&lon=174.763336";
    private static final String DETAILS_PATH = "?lat=-36.848461&lon=174.763336";

    private final String url;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public WeatherLambdaProcessor(final String url, final String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
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
}
