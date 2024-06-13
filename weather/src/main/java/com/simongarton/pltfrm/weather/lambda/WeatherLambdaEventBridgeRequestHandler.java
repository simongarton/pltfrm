package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.platform.factory.LambdaRequestHandlerFactory;
import com.simongarton.platform.factory.PltfrmCommonFactory;
import com.simongarton.platform.model.APIMethod;
import com.simongarton.platform.model.APIStatusCode;
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WeatherLambdaEventBridgeRequestHandler implements RequestHandler<ScheduledEvent, String> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaEventBridgeRequestHandler.class);

    private static final String WEATHER_URL = "/pltfrm/openweathermap-url";
    private static final String WEATHER_API_KEY = "/pltfrm/openweathermap-api-key";

    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;
    final private WeatherLambdaProcessor processor;
    final private ObjectMapper objectMapper;

    public WeatherLambdaEventBridgeRequestHandler() {
        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        final String url = pltfrmSSMService.getParameterValue(WEATHER_URL);
        final String apiKey = pltfrmSSMService.getSecureParameterValue(WEATHER_API_KEY);
        this.processor = new WeatherLambdaProcessor(url, apiKey);
        this.lambdaRequestHandlerFactory = PltfrmCommonFactory.getLambdaRequestHandlerFactory();
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public String handleRequest(final ScheduledEvent scheduledEvent,
                                final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());


        try {

            LOG.info(this.objectMapper.writeValueAsString(scheduledEvent));

            return this.objectMapper.writeValueAsString(scheduledEvent);


        } catch (final Exception e) {

            LOG.error(e.getMessage());

            return e.getMessage();

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
