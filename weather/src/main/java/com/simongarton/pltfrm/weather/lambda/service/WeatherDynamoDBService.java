package com.simongarton.pltfrm.weather.lambda.service;

import com.simongarton.pltfrm.service.PltfrmDynamoDBService;
import com.simongarton.pltfrm.utils.DateTimeUtils;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.PltfrmWeatherLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class WeatherDynamoDBService extends PltfrmDynamoDBService {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmDynamoDBService.class);

    public static final String PLATFORM_WEATHER_LOG_TABLE = "PltfrmWeatherLog";
    public static final String PLATFORM_WEATHER_WEATHER_TABLE = "PltfrmWeatherWeather";
    public static final String PLATFORM_WEATHER_FORECAST_HOUR_TABLE = "PltfrmWeatherForecastHour";
    public static final String PLATFORM_WEATHER_FORECAST_DAY_TABLE = "PltfrmWeatherForecastDay";

    private final DynamoDbTable<PltfrmWeatherLog> logTable;
    private final DynamoDbTable<HourForecast> hourForecastTable;
    private final DynamoDbTable<DayForecast> dayForecastTable;

    public WeatherDynamoDBService() {

        super();
        this.logTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_LOG_TABLE, TableSchema.fromBean(PltfrmWeatherLog.class));
        this.hourForecastTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_FORECAST_HOUR_TABLE, TableSchema.fromBean(HourForecast.class));
        this.dayForecastTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_FORECAST_DAY_TABLE, TableSchema.fromBean(DayForecast.class));
    }

    /*

    I want to use the new enhanced client, but I cannot figure out an exception with key values.

    I'm following https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/ddb-en-client-use.html

    and it looks clear, but I keep getting "The provided key element does not match the schema."

    Repeated attempts, variations, all in vain. Rest of page currently broken and unused.

    Hah, the example on that page is wrong. I thought it might need a sort value. If I add it in, it works.

    Ok, solved. It was the table name I think - I was trying to create a table name of "pltfrm-weather-log-table" and
    once I updated it to be a boring "PltfrmWeatherLog" it worked.

    */

    public String getLog(final String tableName) {

        final Key key = Key.builder()
                .partitionValue(tableName)
                .build();
        final PltfrmWeatherLog pltfrmWeatherLog = this.logTable.getItem(key);

        if (pltfrmWeatherLog != null) {
            LOG.info("Found pltfrmWeatherLog {} for {}", pltfrmWeatherLog.getTimestamp(), tableName);
            return pltfrmWeatherLog.getTimestamp();
        }
        LOG.warn("No pltfrmWeatherLog found for {}", tableName);
        return null;
    }

    public void setLog(final String tableName, final String timestamp) {

        final PltfrmWeatherLog pltfrmWeatherLog = this.logTable.getItem(Key.builder().partitionValue(tableName).build());
        if (pltfrmWeatherLog != null) {
            pltfrmWeatherLog.setTimestamp(timestamp);
            pltfrmWeatherLog.setActualTime(
                    DateTimeUtils.asOffsetDateTimeString(
                            DateTimeUtils.inPacificAuckland(OffsetDateTime.now())
                                    .truncatedTo(ChronoUnit.SECONDS)));
            this.logTable.putItem(pltfrmWeatherLog);
        } else {
            final PltfrmWeatherLog newPltfrmWeatherLog = new PltfrmWeatherLog();
            newPltfrmWeatherLog.setId(tableName);
            newPltfrmWeatherLog.setTimestamp(timestamp);
            newPltfrmWeatherLog.setActualTime(
                    DateTimeUtils.asOffsetDateTimeString(
                            DateTimeUtils.inPacificAuckland(OffsetDateTime.now())
                                    .truncatedTo(ChronoUnit.SECONDS)));
            this.logTable.putItem(newPltfrmWeatherLog);
        }
    }

    public void putHourForecast(final HourForecast hourForecast) {
        this.hourForecastTable.putItem(hourForecast);
    }

    public void putDayForecast(final DayForecast dayForecast) {
        this.dayForecastTable.putItem(dayForecast);
    }

    public DayForecast getDayForecast(final String timestamp) {

        final Key key = Key.builder()
                .partitionValue(timestamp)
                .build();
        final DayForecast dayForecast = this.dayForecastTable.getItem(key);
        LOG.info("Looking for day forecast for {} and found {}", timestamp, dayForecast);
        return dayForecast;
    }

    public HourForecast getHourForecast(final String timestamp) {

        final Key key = Key.builder()
                .partitionValue(timestamp)
                .build();
        final HourForecast hourForecast = this.hourForecastTable.getItem(key);
        LOG.info("Looking for hour forecast for {} and found {}", timestamp, hourForecast);
        return hourForecast;
    }
}
