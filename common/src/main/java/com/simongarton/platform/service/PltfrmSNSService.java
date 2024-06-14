package com.simongarton.platform.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.simongarton.platform.factory.AWSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PltfrmSNSService {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmSNSService.class);

    private final AmazonSNS client;

    public PltfrmSNSService() {
        this.client = AWSFactory.getSNSClient();
    }

    public String publish(final String message, final String topicArn) {
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        final PublishResult publishResult = this.client.publish(publishRequest);
        LOG.info("published " + message + " to " + topicArn);
        return publishResult.toString();
    }

}
