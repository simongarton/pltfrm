package com.simongarton.pltfrm.weather.lambda.processor;

import com.simongarton.platform.service.PltfrmS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

class WeatherLambdaProcessorTest {

    private String url;
    private String apiKey;
    private PltfrmS3Service pltfrmS3Service;

    @BeforeEach
    void setUp() throws IOException {

        final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("")).getPath();
        final String appConfigPath = rootPath + "application-local.properties";

        final Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));

        this.url = appProps.getProperty("url");
        this.apiKey = appProps.getProperty("apiKey");

        this.pltfrmS3Service = mock(PltfrmS3Service.class);
    }

    @Test
    void getWeatherSummary() {
        // given
        final WeatherLambdaProcessor weatherLambdaProcessor = new WeatherLambdaProcessor(
                this.url,
                this.apiKey,
                this.pltfrmS3Service);

        // when
        final String actual = weatherLambdaProcessor.getWeatherSummary();

        // then
        assertThat(actual, is(notNullValue()));
        System.out.println(actual);
    }

    @Test
    void getWeatherDetails() {
        // given
        final WeatherLambdaProcessor weatherLambdaProcessor = new WeatherLambdaProcessor(
                this.url,
                this.apiKey,
                this.pltfrmS3Service);

        // when
        final String actual = weatherLambdaProcessor.getWeatherDetails();

        // then
        assertThat(actual, is(notNullValue()));
        System.out.println(actual);
    }

    @Test
    void debugWeatherDates() {
        // given
        final WeatherLambdaProcessor weatherLambdaProcessor = new WeatherLambdaProcessor(this.url,
                this.apiKey,
                this.pltfrmS3Service);

        // when
        weatherLambdaProcessor.debugWeatherDates();

        // then
        assertThat(true, is(true));
    }
}