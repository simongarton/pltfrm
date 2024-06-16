package com.simongarton.pltfrm.weather.lambda.model.pltfrmweather;

import lombok.Data;

import java.util.List;

@Data
public class RainHourForecast {

    private List<RainMinute> rainMinutes;
}
