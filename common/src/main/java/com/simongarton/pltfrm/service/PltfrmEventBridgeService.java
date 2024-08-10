package com.simongarton.pltfrm.service;

import com.simongarton.pltfrm.factory.AWSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;

public class PltfrmEventBridgeService {

    private final EventBridgeClient eventBridgeClient;

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmEventBridgeService.class.getSimpleName());

    public PltfrmEventBridgeService() {
        this.eventBridgeClient = AWSFactory.getEventBridgeClient();
        LOG.info("Built EventBridgeService");
    }

    public void putEvent(final String busName, final String source, final String detailType, final String detail) {
        LOG.info("Putting event for " + source + ":" + detailType + ":" + detail);
        this.eventBridgeClient.putEvents(
                PutEventsRequest.builder()
                        .entries(
                                PutEventsRequestEntry.builder()
                                        .source(source)
                                        .detailType(detailType)
                                        .detail(detail)
                                        .eventBusName(busName)
                                        .build()
                        )
                        .build()
        );
    }
}
