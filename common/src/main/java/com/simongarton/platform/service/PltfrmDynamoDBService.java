package com.simongarton.platform.service;

import com.simongarton.platform.factory.AWSFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class PltfrmDynamoDBService {


    protected final DynamoDbClient dynamoDBClient;
    protected final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public PltfrmDynamoDBService() {
        this.dynamoDBClient = AWSFactory.getDynamoDBClient();
        this.dynamoDbEnhancedClient = AWSFactory.getDynamoDBEnhancedClient(this.dynamoDBClient);
    }

    public void putItem(final Map<String, Object> itemMap, final String tableName) {

        final Map<String, AttributeValue> item = new HashMap<>();
        for (final Map.Entry<String, Object> entry : itemMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                item.put(entry.getKey(), AttributeValue.builder().s((String) entry.getValue()).build());
            }
            if (entry.getValue() instanceof Number) {
                item.put(entry.getKey(), AttributeValue.builder().n((String) entry.getValue()).build());
            }
            if (entry.getValue() instanceof Map) {
                final Map<String, AttributeValue> map = new HashMap<>();
                final Map<String, String> actualItemMap = (Map<String, String>) entry.getValue();
                for (final Map.Entry<String, String> itemMapEntry : actualItemMap.entrySet()) {
                    map.put(itemMapEntry.getKey(), AttributeValue.builder().s(itemMapEntry.getValue()).build());
                }
                item.put(entry.getKey(), AttributeValue.builder().m(map).build());
            }
        }
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        this.dynamoDBClient.putItem(putItemRequest);
    }

    public Map<String, AttributeValue> getItem(final Map<String, String> keyMap, final String tableName) {

        final Map<String, AttributeValue> item = new HashMap<>();
        for (final Map.Entry<String, String> entry : keyMap.entrySet()) {
            item.put(entry.getKey(), AttributeValue.builder().s(entry.getValue()).build());
        }
        final GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(item)
                .build();
        final GetItemResponse getItemResponse = this.dynamoDBClient.getItem(getItemRequest);
        return getItemResponse.item();
    }
}
