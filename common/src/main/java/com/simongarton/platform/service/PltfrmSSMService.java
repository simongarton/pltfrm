package com.simongarton.platform.service;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException;
import com.simongarton.platform.factory.AWSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PltfrmSSMService {

    private static final Logger LOG = LoggerFactory.getLogger(PltfrmSSMService.class.getSimpleName());

    private final AWSSimpleSystemsManagement client;

    public PltfrmSSMService() {
        this.client = AWSFactory.getSSMClient();
    }

    public String getParameterValue(final String parameter) {
        final GetParameterRequest getParameterRequest = new GetParameterRequest()
                .withName(parameter);
        try {
            LOG.info("Looking for Parameter: " + parameter);
            final GetParameterResult getParameterResult = this.client.getParameter(getParameterRequest);
            return getParameterResult.getParameter().getValue();
        } catch (final ParameterNotFoundException e) {
            LOG.error("Parameter not found: " + parameter);
            throw e;
        }
    }

    public String getSecureParameterValue(final String parameter) {
        final GetParameterRequest getParameterRequest = new GetParameterRequest()
                .withName(parameter)
                .withWithDecryption(true);
        try {
            LOG.info("Looking for SecureParameter: " + parameter);
            final GetParameterResult getParameterResult = this.client.getParameter(getParameterRequest);
            return getParameterResult.getParameter().getValue();
        } catch (final ParameterNotFoundException e) {
            LOG.error("SecureParameter not found: " + parameter);
            throw e;
        }
    }
}
