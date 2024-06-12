# pltfrm

Welcome to `pltfrm` : an AWS platform, designed as a portfolio, demonstration site, and a chance to explore new
technologies and ideas.

## Overview

### Stage 1 : Wthr

This stage is a simple weather application, which uses the OpenWeatherMap API to retrieve weather data for a given
location - where I live. It exposes APIs to call for current and forecasted weather, and also stores data in Timescale,
AWS's time-series database, that allows me to (a) build up history, and also (b) see how forecasts can change over time.

Endpoints:

```
GET {url}/weather : text description of the weather now in Auckland.
```

## Software used

- Java : Corretto 17
- Lombok : for boilerplate code
- Jackson : JSON serialization/deserialization
- Log4J2 : logging library

- Squidfunk : https://github.com/squidfunk/terraform-aws-api-gateway-enable-cors

## Notes

I wanted to use AWS v2 exclusively ... but it doesn't look like all the functionality is there yet. So I'm using v1 for
now.
e.g. https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html