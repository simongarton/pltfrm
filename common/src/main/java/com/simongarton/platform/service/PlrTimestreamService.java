package com.simongarton.platform.service;

import com.simongarton.platform.factory.PltfrmCommonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class PlrTimestreamService {

    private static final Logger LOG = LoggerFactory.getLogger(PlrTimestreamService.class);

    private final TimestreamWriteClient timestreamWriteClient;
    private final TimestreamQueryClient timestreamQueryClient;
    private final DateTimeFormatter dateTimeFormatter;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public PlrTimestreamService() {
        this.timestreamWriteClient = buildWriteClient();
        this.timestreamQueryClient = TimestreamQueryClient.builder().build();
        this.dateTimeFormatter = PltfrmCommonFactory.getDateTimeFormatter();
    }

    private static TimestreamWriteClient buildWriteClient() {

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
                .region(Region.AP_SOUTHEAST_2)
                .build();
    }
}
