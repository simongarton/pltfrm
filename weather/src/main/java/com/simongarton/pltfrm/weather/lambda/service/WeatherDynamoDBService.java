package com.simongarton.pltfrm.weather.lambda.service;

import com.simongarton.platform.service.PltfrmDynamoDBService;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.PltfrmWeatherLog;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class WeatherDynamoDBService extends PltfrmDynamoDBService {

    public static final String PLATFORM_WEATHER_LOG_TABLE = "PltfrmWeatherLog";
    public static final String PLATFORM_WEATHER_WEATHER_TABLE = "PltfrmWeatherWeather";
    public static final String PLATFORM_WEATHER_FORECAST_HOUR_TABLE = "PltfrmWeatherForecastHour";
    public static final String PLATFORM_WEATHER_FORECAST_DAY_TABLE = "PltfrmWeatherForecastDay";

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

        final DynamoDbTable<PltfrmWeatherLog> logTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_LOG_TABLE, TableSchema.fromBean(PltfrmWeatherLog.class));

        final Key key = Key.builder()
                .partitionValue(tableName)
                .build();
        final PltfrmWeatherLog pltfrmWeatherLog = logTable.getItem(key);

        if (pltfrmWeatherLog != null) {
            return pltfrmWeatherLog.getTimestamp();
        }
        return null;
    }

    public void setLog(final String tableName, final String timestamp) {

        final DynamoDbTable<PltfrmWeatherLog> logTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_LOG_TABLE, TableSchema.fromBean(PltfrmWeatherLog.class));
        final PltfrmWeatherLog pltfrmWeatherLog = logTable.getItem(Key.builder().partitionValue(tableName).build());
        if (pltfrmWeatherLog != null) {
            pltfrmWeatherLog.setTimestamp(timestamp);
            logTable.putItem(pltfrmWeatherLog);
        } else {
            final PltfrmWeatherLog newPltfrmWeatherLog = new PltfrmWeatherLog();
            newPltfrmWeatherLog.setId(tableName);
            newPltfrmWeatherLog.setTimestamp(timestamp);
            logTable.putItem(newPltfrmWeatherLog);
        }
    }
}
