package com.simongarton.platform.factory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.simongarton.platform.model.APIMessage;
import com.simongarton.platform.model.APIStatusCode;
import com.simongarton.platform.service.PltfrmCloudwatchService;

import java.util.HashMap;
import java.util.Map;

public class LambdaRequestHandlerFactory {

    final private PltfrmCloudwatchService cloudwatchService;

    public LambdaRequestHandlerFactory() {
        this.cloudwatchService = new PltfrmCloudwatchService();
    }

    public APIGatewayProxyResponseEvent buildResponse(final Object o, final int statusCode) {
        final APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            responseEvent.setBody(PltfrmCommonFactory.getObjectMapper().writeValueAsString(o));
        } catch (final JsonProcessingException e) {
            responseEvent.setBody(e.getMessage());
        }
        final Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "*");
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }

    public APIGatewayProxyResponseEvent standardResponse(final APIStatusCode apiStatusCode,
                                                         final String className,
                                                         final String method,
                                                         final String message) {
        this.cloudwatchService.addCount(
                PltfrmCloudwatchService.API,
                className,
                method,
                apiStatusCode.toString());

        return this.buildResponse(APIMessage.builder()
                        .httpCode(apiStatusCode.getStatusCode())
                        .message(message)
                        .build(),
                apiStatusCode.getStatusCode());
    }
}
