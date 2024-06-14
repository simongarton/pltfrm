package com.simongarton.pltfrm.weather.lambda.model.factory;

import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import com.simongarton.pltfrm.weather.lambda.service.WeatherTimestreamService;

public class WeatherServiceFactory {

    public static WeatherDynamoDBService getWeatherDynamoDBService() {
        return new WeatherDynamoDBService();
    }

    public static WeatherTimestreamService getWeatherTimestreamService() {
        return new WeatherTimestreamService();
    }
}


