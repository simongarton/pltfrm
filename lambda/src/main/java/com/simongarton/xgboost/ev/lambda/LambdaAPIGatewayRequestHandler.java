package com.simongarton.xgboost.ev.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.LambdaRequestHandlerFactory;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.model.APIMethod;
import com.simongarton.platform.model.APIStatusCode;
import com.simongarton.platform.service.PltfrmCloudwatchService;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.xgboost.ev.lambda.processor.LambdaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class LambdaAPIGatewayRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(LambdaAPIGatewayRequestHandler.class);


    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;
    final private PltfrmCloudwatchService cloudwatchService;
    final private LambdaProcessor processor;
    final private ObjectMapper objectMapper;

    public LambdaAPIGatewayRequestHandler() {

        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        this.processor = new LambdaProcessor();
        this.lambdaRequestHandlerFactory = PltfrmCommonFactory.getLambdaRequestHandlerFactory();
        this.cloudwatchService = PltfrmCommonFactory.getPltfrmCloudwatchService();
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent,
                                                      final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        LOG.info("APIGatewayProxyRequestEvent: {}", apiGatewayProxyRequestEvent);
        final String method = apiGatewayProxyRequestEvent.getHttpMethod();
        final Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
        try {
            if (method.equalsIgnoreCase(APIMethod.GET.getMethod())) {
                if (queryStringParameters == null || queryStringParameters.isEmpty()) {
                    return this.getBasicResponse();
                }
                for (final Map.Entry<String, String> entry : queryStringParameters.entrySet()) {
                    LOG.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
                LOG.error("Unknown parameters.");
                return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.BAD_REQUEST,
                        this.getClass().getSimpleName(),
                        method,
                        APIStatusCode.BAD_REQUEST.toString());
            }

            LOG.error("Unknown method " + method);
            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.UNKNOWN_METHOD,
                    this.getClass().getSimpleName(),
                    method,
                    APIStatusCode.UNKNOWN_METHOD.toString());

        } catch (final Exception e) {

            LOG.error(e.getMessage(), e);
            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.INTERNAL_SERVER_ERROR,
                    this.getClass().getSimpleName(),
                    method,
                    e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent getBasicResponse() {
        return this.buildResponse("Hello world !", 200);
    }

    private APIGatewayProxyResponseEvent buildResponse(final Object o, final int statusCode) {
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
}
