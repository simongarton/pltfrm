package com.simongarton.pltfrm.weather.lambda;


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
import com.simongarton.platform.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.factory.WeatherServiceFactory;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.processor.OpenWeatherMapClient;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaAPIGatewayProcessor;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

public class WeatherLambdaAPIGatewayRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaAPIGatewayRequestHandler.class);

    private static final String WEATHER_URL = "/pltfrm/openweathermap-url";
    private static final String WEATHER_API_KEY = "/pltfrm/openweathermap-api-key";

    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;
    final private WeatherLambdaAPIGatewayProcessor processor;
    final private ObjectMapper objectMapper;

    public WeatherLambdaAPIGatewayRequestHandler() {
        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        final String url = pltfrmSSMService.getParameterValue(WEATHER_URL);
        final String apiKey = pltfrmSSMService.getSecureParameterValue(WEATHER_API_KEY);
        final OpenWeatherMapClient openWeatherMapClient = new OpenWeatherMapClient(url, apiKey);
        final WeatherDynamoDBService weatherDynamoDBService = WeatherServiceFactory.getWeatherDynamoDBService();
        this.processor = new WeatherLambdaAPIGatewayProcessor(openWeatherMapClient, weatherDynamoDBService);
        this.lambdaRequestHandlerFactory = PltfrmCommonFactory.getLambdaRequestHandlerFactory();
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
                    return this.getWeatherResponse(apiGatewayProxyRequestEvent);
                }
                if (queryStringParameters.containsKey("forecast")) {
                    return this.getWeatherForecastResponse(apiGatewayProxyRequestEvent);
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

    private APIGatewayProxyResponseEvent getWeatherForecastResponse(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws JsonProcessingException {

        final Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
        final String forecastType = queryStringParameters.get("forecast");
        if (forecastType.equalsIgnoreCase("hour")) {
            final HourForecast hourForecast = this.processor.getWeatherHourForecast();
            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                    this.getClass().getSimpleName(),
                    APIMethod.GET.getMethod(),
                    this.objectMapper.writeValueAsString(hourForecast)
            );
        }
        if (forecastType.equalsIgnoreCase("day")) {
            final DayForecast dayForecast = this.processor.getWeatherDayForecast();
            return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                    this.getClass().getSimpleName(),
                    APIMethod.GET.getMethod(),
                    this.objectMapper.writeValueAsString(dayForecast)
            );
        }
        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.BAD_REQUEST,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                "Unknown forecast type: " + forecastType);

    }

    private APIGatewayProxyResponseEvent getWeatherResponse(final APIGatewayProxyRequestEvent event) {

        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                this.processor.getWeatherSummary()
        );
    }
}
