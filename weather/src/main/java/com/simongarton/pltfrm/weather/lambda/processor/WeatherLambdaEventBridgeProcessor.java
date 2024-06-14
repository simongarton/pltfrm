package com.simongarton.pltfrm.weather.lambda.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.service.PltfrmS3Service;
import com.simongarton.platform.service.PltfrmSNSService;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.platform.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.FileNotification;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherCurrentAndForecast;

import java.io.IOException;
import java.net.URISyntaxException;

public class WeatherLambdaEventBridgeProcessor {

    private static final String WEATHER_TOPIC_ARN = "/pltfrm/weather-topic-arn";
    private static final String WEATHER_BUCKET_NAME = "/pltfrm/weather-bucket-name";

    private final ObjectMapper objectMapper;
    private final PltfrmS3Service s3Service;
    private final PltfrmSNSService snsService;
    private final OpenWeatherMapClient openWeatherMapClient;

    private final String topicArn;
    private final String bucketName;

    public WeatherLambdaEventBridgeProcessor(
            final PltfrmSNSService snsService,
            final PltfrmS3Service s3Service,
            final OpenWeatherMapClient openWeatherMapClient
    ) {
        this.snsService = snsService;
        this.s3Service = s3Service;
        this.openWeatherMapClient = openWeatherMapClient;
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();

        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        this.topicArn = pltfrmSSMService.getParameterValue(WEATHER_TOPIC_ARN);
        this.bucketName = pltfrmSSMService.getParameterValue(WEATHER_BUCKET_NAME);
    }

    public void downloadWeatherData() throws URISyntaxException, IOException, InterruptedException {

        final String filename = String.format("weather/%s.json", DateTimeUtils.nowAsFileNameISOinUTC());
        final WeatherCurrentAndForecast weatherCurrentAndForecast = this.openWeatherMapClient.getWeatherDetailsFromAPI();
        this.s3Service.save(weatherCurrentAndForecast,
                filename,
                this.bucketName);
        final FileNotification fileNotification = FileNotification.builder()
                .bucket(this.bucketName)
                .key(filename)
                .build();
        this.snsService.publish(this.objectMapper.writeValueAsString(fileNotification), this.topicArn);
    }
}
