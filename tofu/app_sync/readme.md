With the current setup and this query ...

```
query MyQuery {
  getWeather(timestamp: "2024-07-14T14:00:00+12:00") {
    Weather
    FeelsLike
    Clouds
    DewPoint
    Uvi
    Visibility
    Rain {
      measurement
      timestamp
    }
  }
}
```

I'm getting this response ...

```
{
  "data": {
    "getWeather": {
      "Weather": "Clouds",
      "FeelsLike": 14.6,
      "Clouds": 75,
      "DewPoint": 10.51,
      "Uvi": 1.4,
      "Visibility": 10000,
      "Rain": null
    }
  },
  "errors": [
    {
      "path": [
        "getWeather",
        "Rain"
      ],
      "locations": null,
      "message": "Can't resolve value (/getWeather/Rain) : type mismatch error, expected type LIST"
    }
  ]
}
```

I _think_ the problem is that Rain is actually a Dict, not a List but GraphQL doesn't support Dicts.
I should probably look at changing the schema to make Rain a List of interval measurments,
but Copilot says I'm not sure how to do that yet. It could just be a weird choice of model
and a weird resolver.