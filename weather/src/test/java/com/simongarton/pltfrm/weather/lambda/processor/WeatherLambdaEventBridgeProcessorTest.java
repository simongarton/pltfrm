package com.simongarton.pltfrm.weather.lambda.processor;

import com.simongarton.pltfrm.service.PltfrmEventBridgeService;
import com.simongarton.pltfrm.service.PltfrmS3Service;
import com.simongarton.pltfrm.service.PltfrmSNSService;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

class WeatherLambdaEventBridgeProcessorTest {

    @Test
    public void showPackageName() {

        // given
        final PltfrmSNSService snsService = mock(PltfrmSNSService.class);
        final PltfrmS3Service s3Service = mock(PltfrmS3Service.class);
        final PltfrmEventBridgeService eventBridgeService = mock(PltfrmEventBridgeService.class);
        final OpenWeatherMapClient openWeatherMapClient = mock(OpenWeatherMapClient.class);

        final WeatherLambdaEventBridgeProcessor weatherLambdaEventBridgeProcessor = new WeatherLambdaEventBridgeProcessor(
                snsService,
                s3Service,
                eventBridgeService,
                openWeatherMapClient
        );

        // when
        final String packageName = weatherLambdaEventBridgeProcessor.getClass().getPackageName();

        // then
        assertThat(packageName, is(equalTo("com.simongarton.pltfrm.weather.lambda.processor")));
    }

}