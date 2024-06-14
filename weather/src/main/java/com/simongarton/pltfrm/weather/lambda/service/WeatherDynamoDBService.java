package com.simongarton.pltfrm.weather.lambda.service;

import com.simongarton.platform.exception.InternalServerErrorException;
import com.simongarton.platform.service.PltfrmDynamoDBService;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.DescribeTableEnhancedResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class WeatherDynamoDBService extends PltfrmDynamoDBService {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherDynamoDBService.class);

    public static final String PLATFORM_WEATHER_LOG_TABLE = "pltfrm-weather-log-table";
    public static final String PLATFORM_WEATHER_WEATHER_TABLE = "pltfrm-weather-weather-table";
    public static final String PLATFORM_WEATHER_FORECAST_HOUR_TABLE = "pltfrm-weather-forecast-hour-table";
    public static final String PLATFORM_WEATHER_FORECAST_DAY_TABLE = "pltfrm-weather-forecast-day-table";

    final DynamoDbTable<Log> logTable;

    public WeatherDynamoDBService() {
        super();

        this.logTable = this.dynamoDbEnhancedClient.table(PLATFORM_WEATHER_LOG_TABLE, TableSchema.fromBean(Log.class));
    }

    private GetItemRequest buildGetItemRequest(final String tableName) {

        final Map<String, AttributeValue> keyItem = new HashMap<>();
        keyItem.put("WeatherTable", AttributeValue.builder().s(tableName).build());
        return GetItemRequest.builder()
                .tableName(PLATFORM_WEATHER_LOG_TABLE)
                .key(keyItem)
                .build();
    }

    public String getLog(final String tableName) {

        final GetItemResponse response = this.dynamoDBClient.getItem(this.buildGetItemRequest(tableName));
        if (response.item().isEmpty()) {
            return null;
        }
        final Map<String, AttributeValue> responseItem = response.item();
        for (final Map.Entry<String, AttributeValue> entry : responseItem.entrySet()) {
            LOG.info("Key is {}", entry.getKey());
            LOG.info("Value is {}", entry.getValue().s());
        }
        for (final Map.Entry<String, AttributeValue> entry : responseItem.entrySet()) {
            if (entry.getKey().equals("Timestamp")) {
                return entry.getValue().s();
            }
        }
        throw new InternalServerErrorException("Timestamp not found in response");
    }

    public void setLog(final String tableName, final String timestamp) {

        final GetItemResponse response = this.dynamoDBClient.getItem(this.buildGetItemRequest(tableName));
        Map<String, AttributeValue> valueItem = new HashMap<>();
        if (response.item().isEmpty()) {
            valueItem.put("WeatherTable", AttributeValue.builder().s(tableName).build());
        } else {
            valueItem = new HashMap<>(response.item());
        }
        valueItem.put("Timestamp", AttributeValue.builder().s(timestamp).build());

        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(PLATFORM_WEATHER_LOG_TABLE)
                .item(valueItem)
                .build();

        this.dynamoDBClient.putItem(putItemRequest);
    }

    /*

    I want to use the new enhanced client, but I cannot figure out an exception with key values.

    I'm following https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/ddb-en-client-use.html

    and it looks clear, but I keep getting "The provided key element does not match the schema."

    Repeated attempts, variations, all in vain. Rest of page currently broken and unused.

    */

    public String getLogNew(final String tableName) {
        LOG.info("tableName is {}", tableName);
        final DescribeTableEnhancedResponse response = this.logTable.describeTable();
        LOG.info(response.toString());
        final Key key = Key.builder()
                .partitionValue(tableName)
                .build();
        LOG.info("partitionKeyValue is {}", key.partitionKeyValue().s());
        // this gives a "The provided key element does not match the schema" but I can't see why ?!
        final Log log = this.logTable.getItem(key);
        // so did this approach
//        final QueryConditional keyEqual = QueryConditional.keyEqualTo(b -> b.partitionValue(key));
//        final QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
//                .queryConditional(keyEqual)
//                .build();
//        LOG.info(queryEnhancedRequest.toString());
//        final PageIterable<Log> queryResponse = this.logTable.query(queryEnhancedRequest);
//        if (queryResponse != null) {
//            for (final Log item : queryResponse.items()) {
//                return item.getTimestamp();
//            }
//        }

        if (log != null) {
            return log.getTimestamp();
        }
        return null;
    }

    public void setLogNew(final String tableName, final String timestamp) {
        final Log log = this.logTable.getItem(Key.builder().partitionValue(tableName).build());
        if (log != null) {
            log.setTimestamp(timestamp);
            this.logTable.putItem(log);
        } else {
            final Log newLog = new Log();
            newLog.setWeatherTable(tableName);
            newLog.setTimestamp(timestamp);
            this.logTable.putItem(newLog);
        }
    }
}
