package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.simongarton.platform.factory.LambdaRequestHandlerFactory;
import com.simongarton.platform.model.APIMethod;
import com.simongarton.platform.model.APIStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WeatherLambdaRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaRequestHandler.class);

    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;

    public WeatherLambdaRequestHandler() {
        this.lambdaRequestHandlerFactory = new LambdaRequestHandlerFactory();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent,
                                                      final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        final String method = apiGatewayProxyRequestEvent.getHttpMethod();
        try {
            if (method.equalsIgnoreCase(APIMethod.GET.getMethod())) {
                return this.getWeatherResponse(apiGatewayProxyRequestEvent);
            }

            LOG.error("Unknown method " + method);

            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.UNKNOWN_METHOD,
                    this.getClass().getSimpleName(),
                    method,
                    APIStatusCode.UNKNOWN_METHOD.toString());

        } catch (final JsonProcessingException e) {

            LOG.error(e.getMessage());

            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.BAD_REQUEST,
                    this.getClass().getSimpleName(),
                    method,
                    e.getMessage());

        } catch (final Exception e) {

            LOG.error(e.getMessage());

            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.INTERNAL_SERVER_ERROR,
                    this.getClass().getSimpleName(),
                    method,
                    e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent getWeatherResponse(final APIGatewayProxyRequestEvent event)
            throws JsonProcessingException {

        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                "Cold and wet today!"
        );
    }
}
