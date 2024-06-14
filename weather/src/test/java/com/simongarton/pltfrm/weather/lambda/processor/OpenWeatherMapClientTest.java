package com.simongarton.pltfrm.weather.lambda.processor;

import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherCurrentAndForecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

class OpenWeatherMapClientTest {

    private String url;
    private String apiKey;

    @BeforeEach
    void setUp() throws IOException {

        final String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResource("")).getPath();
        final String appConfigPath = rootPath + "application-local.properties";

        final Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));

        this.url = appProps.getProperty("url");
        this.apiKey = appProps.getProperty("apiKey");
    }


    @Test
    void getWeatherSummaryFromAPI() throws URISyntaxException, IOException, InterruptedException {

        // given
        final OpenWeatherMapClient openWeatherMapClient = new OpenWeatherMapClient(this.url, this.apiKey);

        // when
        final String actual = openWeatherMapClient.getWeatherSummaryFromAPI();

        // then
        assertThat(actual, is(notNullValue()));
    }

    @Test
    void getWeatherDetailsFromAPI() throws URISyntaxException, IOException, InterruptedException {

        // given
        final OpenWeatherMapClient openWeatherMapClient = new OpenWeatherMapClient(this.url, this.apiKey);

        // when
        final WeatherCurrentAndForecast actual = openWeatherMapClient.getWeatherDetailsFromAPI();

        // then
        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCurrent(), is(notNullValue()));
        assertThat(actual.getMinuteList(), not(Collections.emptyList()));
        assertThat(actual.getHourList(), not(Collections.emptyList()));
        assertThat(actual.getDayList(), not(Collections.emptyList()));
    }
}