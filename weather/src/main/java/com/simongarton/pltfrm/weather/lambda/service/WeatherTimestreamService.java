package com.simongarton.pltfrm.weather.lambda.service;

import com.simongarton.platform.service.PltfrmTimestreamService;
import com.simongarton.platform.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.Weather;
import com.simongarton.pltfrm.weather.lambda.model.openweathermap.WeatherCurrentAndForecast;
import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.MeasureValueType;
import software.amazon.awssdk.services.timestreamwrite.model.Record;
import software.amazon.awssdk.services.timestreamwrite.model.TimeUnit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherTimestreamService extends PltfrmTimestreamService {

    public static final String PLATFORM_WEATHER_TABLE_NAME = "pltfrm-weather-day-table";

    public int saveData(final WeatherCurrentAndForecast weatherCurrentAndForecast,
                        final String databaseName,
                        final String tableName) {

        final List<Record> records = new ArrayList<>();

        final OffsetDateTime midnightEndOfDay = DateTimeUtils.inPacificAuckland(
                DateTimeUtils.longToOffsetDateTime(weatherCurrentAndForecast.getCurrent().getDt())
                        .truncatedTo(ChronoUnit.DAYS)
                        .plusDays(1));
        final OffsetDateTime currentHour = DateTimeUtils.inPacificAuckland(
                OffsetDateTime.now()
                        .truncatedTo(ChronoUnit.HOURS));
        final OffsetDateTime timestamp = DateTimeUtils.inPacificAuckland(
                OffsetDateTime.now());

        final String weather = weatherCurrentAndForecast.getCurrent().getWeather().stream().map(Weather::getMain).collect(Collectors.joining(","));

        final Dimension latitudeDimension = Dimension.builder().name("latitude").value(String.valueOf(weatherCurrentAndForecast.getLat())).build();
        final Dimension longitudeDimension = Dimension.builder().name("longitude").value(String.valueOf(weatherCurrentAndForecast.getLon())).build();
        final Dimension timezoneDimension = Dimension.builder().name("timezone").value(String.valueOf(weatherCurrentAndForecast.getTimezone())).build();
        final Dimension midnightDimension = Dimension.builder().name("midnight").value(this.dateTimeFormatter.format(midnightEndOfDay)).build();
        final Dimension hourDimension = Dimension.builder().name("hour").value(this.dateTimeFormatter.format(currentHour)).build();
        final Dimension timestampDimension = Dimension.builder().name("timestamp").value(this.dateTimeFormatter.format(timestamp)).build();
        final Dimension weatherDimension = Dimension.builder().name("weather").value(weather).build();

        final Record commonAttributes = Record.builder()
                .dimensions(List.of(latitudeDimension,
                        longitudeDimension,
                        timezoneDimension,
                        midnightDimension,
                        hourDimension,
                        timestampDimension,
                        weatherDimension))
                .measureValueType(MeasureValueType.DOUBLE)
                .version(System.currentTimeMillis())
                .build();

        // this now turns out to be a bit pointless, because everything in Timestream has to be in the past, and all
        // of this data is forecasts, in the future.

        final Record record = Record.builder()
                .time(String.valueOf(this.getCorrectUTCSecond(OffsetDateTime.now())))
                .timeUnit(TimeUnit.SECONDS)
                .measureName("temperature")
                .measureValue(String.valueOf(weatherCurrentAndForecast.getCurrent().getTemp()))
                .build();
        records.add(record);

        return this.writeRecordsInBatchesOf100(commonAttributes, records, databaseName, tableName);
    }

}
