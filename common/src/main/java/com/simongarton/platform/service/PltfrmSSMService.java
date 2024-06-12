package com.simongarton.platform.service;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;

public class PltfrmSSMService {

    private final AWSSimpleSystemsManagement client;

    public PltfrmSSMService() {
        this.client = AWSSimpleSystemsManagementClientBuilder
                .standard()
                .build();
    }

    public String getParameterValue(final String parameter) {
        final GetParameterRequest getParameterRequest = new GetParameterRequest()
                .withName(parameter);
        final GetParameterResult getParameterResult = this.client.getParameter(getParameterRequest);
        return getParameterResult.getParameter().getValue();
    }

    public String getSecureParameterValue(final String parameter) {
        final GetParameterRequest getParameterRequest = new GetParameterRequest()
                .withName(parameter)
                .withWithDecryption(true);
        final GetParameterResult getParameterResult = this.client.getParameter(getParameterRequest);
        return getParameterResult.getParameter().getValue();
    }
}
