package com.simongarton.pltfrm.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.lambda.model.SQSMessageBody;
import com.simongarton.pltfrm.lambda.processor.LambdaProcessor;
import com.simongarton.pltfrm.service.PltfrmS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LambdaSQSEventRequestHandler implements RequestHandler<SQSEvent, String> {

    private static final Logger LOG = LoggerFactory.getLogger(LambdaSQSEventRequestHandler.class);

    final private LambdaProcessor processor;
    final private ObjectMapper objectMapper;

    public LambdaSQSEventRequestHandler() {

        final PltfrmS3Service pltfrmS3Service = PltfrmCommonFactory.getPltfrmS3Service();
        this.processor = new LambdaProcessor();
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public String handleRequest(final SQSEvent sqsEvent,
                                final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        try {
            for (final SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
                final SQSMessageBody sqsMessageBody = this.objectMapper.readValue(message.getBody(), SQSMessageBody.class);
                this.processor.process();
            }
            return "Processed " + sqsEvent.getRecords().size();

        } catch (final Exception e) {

            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}