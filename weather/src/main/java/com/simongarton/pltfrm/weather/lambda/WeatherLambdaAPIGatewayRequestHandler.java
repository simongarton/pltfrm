package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.simongarton.platform.factory.LambdaRequestHandlerFactory;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.model.APIMethod;
import com.simongarton.platform.model.APIStatusCode;
import com.simongarton.platform.service.PltfrmS3Service;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WeatherLambdaAPIGatewayRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaAPIGatewayRequestHandler.class);

    private static final String WEATHER_URL = "/pltfrm/openweathermap-url";
    private static final String WEATHER_API_KEY = "/pltfrm/openweathermap-api-key";

    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;
    final private WeatherLambdaProcessor processor;

    public WeatherLambdaAPIGatewayRequestHandler() {
        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        final String url = pltfrmSSMService.getParameterValue(WEATHER_URL);
        final String apiKey = pltfrmSSMService.getSecureParameterValue(WEATHER_API_KEY);
        final PltfrmS3Service pltfrmS3Service = PltfrmCommonFactory.getPltfrmS3Service();
        this.processor = new WeatherLambdaProcessor(url, apiKey, pltfrmS3Service);
        this.lambdaRequestHandlerFactory = PltfrmCommonFactory.getLambdaRequestHandlerFactory();
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

        } catch (final Exception e) {

            LOG.error(e.getMessage());

            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.INTERNAL_SERVER_ERROR,
                    this.getClass().getSimpleName(),
                    method,
                    e.getMessage());
        }
    }

    private APIGatewayProxyResponseEvent getWeatherResponse(final APIGatewayProxyRequestEvent event) {

        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                this.processor.getWeatherSummary()
        );
    }
}
