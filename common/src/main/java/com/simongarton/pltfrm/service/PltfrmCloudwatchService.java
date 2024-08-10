package com.simongarton.pltfrm.service;

import com.simongarton.pltfrm.factory.AWSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.util.List;

public class PltfrmCloudwatchService {

    public static final String API = "API";

    private static final String SOURCE = "source";
    private static final String METHOD = "method";
    private static final String OUTCOME = "outcome";


    private final CloudWatchClient cloudWatch;

    // this will vary, so not common code
    private final String NAMESPACE = "pltfrm";

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmCloudwatchService.class.getSimpleName());

    public PltfrmCloudwatchService() {
        this.cloudWatch = AWSFactory.getCloudWatchClient();
        LOG.info("Built CloudWatchService");
    }

    public void addCount(final String metricName,
                         final String source,
                         final String method,
                         final String outcome) {

        LOG.info("Adding metric for " + metricName + ":" + source + ":" + method + ":" + outcome);

        final Dimension sourceDimension = Dimension.builder()
                .name(SOURCE)
                .value(source)
                .build();

        final Dimension methodDimension = Dimension.builder()
                .name(METHOD)
                .value(method)
                .build();

        final Dimension outcomeDimension = Dimension.builder()
                .name(OUTCOME)
                .value(outcome)
                .build();

        final List<Dimension> dimensionList;
        dimensionList = List.of(sourceDimension, methodDimension, outcomeDimension);

        final MetricDatum metricDatum = MetricDatum.builder()
                .metricName(metricName)
                .value(1D)
                .unit(StandardUnit.COUNT)
                .dimensions(dimensionList)
                .timestamp(Instant.now())
                .build();

        final PutMetricDataRequest putMetricDataRequest = PutMetricDataRequest.builder()
                .namespace(this.NAMESPACE)
                .metricData(metricDatum)
                .build();

        final PutMetricDataResponse putMetricDataResponse = this.cloudWatch.putMetricData(putMetricDataRequest);
    }
}
