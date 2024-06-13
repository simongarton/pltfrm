package com.simongarton.pltfrm.weather.lambda.processor;


import com.simongarton.platform.exception.InternalServerErrorException;

import java.io.IOException;
import java.net.URISyntaxException;

public class WeatherLambdaAPIGatewayProcessor {

    private final OpenWeatherMapClient openWeatherMapClient;

    public WeatherLambdaAPIGatewayProcessor(
            final OpenWeatherMapClient openWeatherMapClient
    ) {
        this.openWeatherMapClient = openWeatherMapClient;
    }

    public String getWeatherSummary() {

        try {
            return this.openWeatherMapClient.getWeatherSummaryFromAPI();
        } catch (final URISyntaxException | IOException | InterruptedException e) {
            throw new InternalServerErrorException("Failed to get weather : " + e.getMessage());
        }
    }
}
