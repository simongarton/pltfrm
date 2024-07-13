package com.simongarton.xgboost.ev.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.xgboost.ev.lambda.processor.LambdaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LambdaEventBridgeRequestHandler implements RequestHandler<ScheduledEvent, String> {

    private static final Logger LOG = LoggerFactory.getLogger(LambdaEventBridgeRequestHandler.class);

    final private LambdaProcessor processor;
    final private ObjectMapper objectMapper;

    public LambdaEventBridgeRequestHandler() {


        this.processor = new LambdaProcessor();
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public String handleRequest(final ScheduledEvent scheduledEvent,
                                final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        try {

            final String result = this.processor.process();
            return this.objectMapper.writeValueAsString(result);

        } catch (final Exception e) {

            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
