package com.simongarton.pltfrm.factory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

import java.time.Duration;

public class AWSFactory {

    final static Region region = Region.AP_SOUTHEAST_2;

    public static AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .build();
    }

    public static CloudWatchClient getCloudWatchClient() {
        return CloudWatchClient.builder()
                .region(region)
                .build();
    }

    public static DynamoDbClient getDynamoDBClient() {
        return DynamoDbClient.builder()
                .region(region)
                .build();
    }

    public static AmazonSNS getSNSClient() {
        return AmazonSNSClientBuilder
                .standard()
                .withRegion(region.id())
                .build();
    }

    public static AWSSimpleSystemsManagement getSSMClient() {
        return AWSSimpleSystemsManagementClientBuilder
                .standard()
                .withRegion(region.id())
                .build();
    }

    public static TimestreamQueryClient getTimestreamQueryClient() {
        return TimestreamQueryClient.builder()
                .region(region)
                .build();
    }

    public static TimestreamWriteClient getTimestreamWriteClient() {

        final ApacheHttpClient.Builder httpClientBuilder =
                ApacheHttpClient.builder();
        httpClientBuilder.maxConnections(5000);

        final RetryPolicy.Builder retryPolicy =
                RetryPolicy.builder();
        retryPolicy.numRetries(10);

        final ClientOverrideConfiguration.Builder overrideConfig =
                ClientOverrideConfiguration.builder();
        overrideConfig.apiCallAttemptTimeout(Duration.ofSeconds(20));
        overrideConfig.retryPolicy(retryPolicy.build());

        return TimestreamWriteClient.builder()
                .httpClientBuilder(httpClientBuilder)
                .overrideConfiguration(overrideConfig.build())
                .region(region)
                .build();
    }

    public static DynamoDbEnhancedClient getDynamoDBEnhancedClient(final DynamoDbClient dynamoDBClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDBClient)
                .build();
    }
}
