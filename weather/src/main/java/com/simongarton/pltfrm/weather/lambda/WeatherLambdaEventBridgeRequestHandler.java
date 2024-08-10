package com.simongarton.pltfrm.weather.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simongarton.pltfrm.factory.PltfrmCommonFactory;
import com.simongarton.pltfrm.service.PltfrmEventBridgeService;
import com.simongarton.pltfrm.service.PltfrmS3Service;
import com.simongarton.pltfrm.service.PltfrmSNSService;
import com.simongarton.pltfrm.service.PltfrmSSMService;
import com.simongarton.pltfrm.weather.lambda.processor.OpenWeatherMapClient;
import com.simongarton.pltfrm.weather.lambda.processor.WeatherLambdaEventBridgeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WeatherLambdaEventBridgeRequestHandler implements RequestHandler<ScheduledEvent, String> {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherLambdaEventBridgeRequestHandler.class);

    private static final String WEATHER_URL = "/pltfrm/openweathermap-url";
    private static final String WEATHER_API_KEY = "/pltfrm/openweathermap-api-key";

    final private WeatherLambdaEventBridgeProcessor processor;

    final private ObjectMapper objectMapper;

    public WeatherLambdaEventBridgeRequestHandler() {
        final PltfrmSSMService pltfrmSSMService = PltfrmCommonFactory.getPltfrmSSMService();
        final String url = pltfrmSSMService.getParameterValue(WEATHER_URL);
        final String apiKey = pltfrmSSMService.getSecureParameterValue(WEATHER_API_KEY);
        final PltfrmS3Service pltfrmS3Service = PltfrmCommonFactory.getPltfrmS3Service();
        final PltfrmSNSService pltfrmSNSService = PltfrmCommonFactory.getPltfrmSNSService();
        final PltfrmEventBridgeService pltfrmEventBridgeService = PltfrmCommonFactory.getPltfrmEventBridgeService();
        final OpenWeatherMapClient openWeatherMapClient = new OpenWeatherMapClient(url, apiKey);

        this.processor = new WeatherLambdaEventBridgeProcessor(pltfrmSNSService,
                pltfrmS3Service,
                pltfrmEventBridgeService,
                openWeatherMapClient);
        this.objectMapper = PltfrmCommonFactory.getObjectMapper();
    }

    @Override
    public String handleRequest(final ScheduledEvent scheduledEvent,
                                final Context context) {

        MDC.put("AWSRequestId", context.getAwsRequestId());

        LOG.info("Starting {} for {}", this.getClass().getSimpleName(), context.getAwsRequestId());

        try {

            this.processor.downloadWeatherData();
            return this.objectMapper.writeValueAsString(scheduledEvent);

        } catch (final Exception e) {

            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
