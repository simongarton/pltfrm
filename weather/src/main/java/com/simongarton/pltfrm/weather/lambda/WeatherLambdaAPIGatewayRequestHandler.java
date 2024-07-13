package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.pltfrm.factory.LambdaRequestHandlerFactory;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.model.APIMethod;
import com.simongarton.pltfrm.model.APIStatusCode;
import com.simongarton.pltfrm.service.PltfrmCloudwatchService;
import com.simongarton.pltfrm.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.factory.WeatherServiceFactory;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.DayForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.HourForecast;
import com.simongarton.pltfrm.weather.lambda.model.pltfrmweather.RainHourForecast;
import com.simongarton.pltfrm.weather.lambda.processor.OpenWeatherMapClient;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaAPIGatewayProcessor;
import com.simongarton.pltfrm.weather.lambda.service.WeatherDynamoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class WeatherLambdaAPIGatewayRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaAPIGatewayRequestHandler.class);

    private static final String WEATHER_URL = "/pltfrm/openweathermap-url";
    private static final String WEATHER_API_KEY = "/pltfrm/openweathermap-api-key";

    final private LambdaRequestHandlerFactory lambdaRequestHandlerFactory;
    final private PltfrmCloudwatchService cloudwatchService;
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
                    return this.getWeatherResponse();
                }
                if (queryStringParameters.containsKey("forecast")) {
                    return this.getWeatherForecastResponse(apiGatewayProxyRequestEvent);
                }
                if (queryStringParameters.containsKey("data")) {
                    return this.getWeatherDataResponse(apiGatewayProxyRequestEvent);
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

            return this.getWeatherForecastHourResponse(apiGatewayProxyRequestEvent);
        }
        if (forecastType.equalsIgnoreCase("day")) {
            return this.getWeatherForecastDayResponse(apiGatewayProxyRequestEvent);
        }
        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.BAD_REQUEST,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                "Unknown forecast type: " + forecastType);

    }

    private APIGatewayProxyResponseEvent getWeatherResponse() {

        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.OK,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                this.processor.getWeatherSummary()
        );
    }

    private APIGatewayProxyResponseEvent getWeatherDataResponse(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws URISyntaxException, IOException, InterruptedException {

        final Map<String, String> queryStringParameters = apiGatewayProxyRequestEvent.getQueryStringParameters();
        final String forecastType = queryStringParameters.get("data");
        if (forecastType.equalsIgnoreCase("rain")) {
            return this.getWeatherDataRainResponse(apiGatewayProxyRequestEvent);
        }

        return this.lambdaRequestHandlerFactory.standardResponse(APIStatusCode.BAD_REQUEST,
                this.getClass().getSimpleName(),
                APIMethod.GET.getMethod(),
                "Unknown data type: " + forecastType);
    }

    private APIGatewayProxyResponseEvent getWeatherForecastHourResponse(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {

        final HourForecast hourForecast = this.processor.getWeatherHourForecast();

        // TODO more metrics for different kind of forecast ?
        this.cloudwatchService.addCount(
                PltfrmCloudwatchService.API,
                this.getClass().getSimpleName(),
                apiGatewayProxyRequestEvent.getHttpMethod(),
                APIStatusCode.OK.toString());

        return this.buildResponse(hourForecast, APIStatusCode.OK.getStatusCode());
    }

    private APIGatewayProxyResponseEvent getWeatherForecastDayResponse(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {

        final DayForecast dayForecast = this.processor.getWeatherDayForecast();

        // TODO more metrics for different kind of forecast ?
        this.cloudwatchService.addCount(
                PltfrmCloudwatchService.API,
                this.getClass().getSimpleName(),
                apiGatewayProxyRequestEvent.getHttpMethod(),
                APIStatusCode.OK.toString());

        return this.buildResponse(dayForecast, APIStatusCode.OK.getStatusCode());
    }

    private APIGatewayProxyResponseEvent getWeatherDataRainResponse(
            final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) throws
            URISyntaxException, IOException, InterruptedException {

        final RainHourForecast rainHourForecast = this.processor.getRainForecast();

        this.cloudwatchService.addCount(
                PltfrmCloudwatchService.API,
                this.getClass().getSimpleName(),
                apiGatewayProxyRequestEvent.getHttpMethod(),
                APIStatusCode.OK.toString());

        return this.buildResponse(rainHourForecast, APIStatusCode.OK.getStatusCode());
    }

    // this is on the request handler factory as well ...
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
