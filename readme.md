# pltfrm

Welcome to `pltfrm` : an AWS platform, designed as a portfolio, demonstration site, and a chance to explore new
technologies and ideas.

## Overview

### Stage 1 : Wthr

This stage is a simple weather application, which uses the OpenWeatherMap API to retrieve weather data for a given
location - where I live. It exposes APIs to call for current and forecasted weather, and also stores data in Timestream,
AWS's time-series database, that allows me to (a) build up history, and also (b) see how forecasts can change over time.

![Part 1](./documentation/images/pltfrm-wthr-1.png)

![Part 2](./documentation/images/pltfrm-wthr-2.png)

Endpoints:

```
GET {url}/weather : text description of the weather now in Auckland.
```

## Front end

I have a simple React/Vite front end available to display some of the data I'm collecting.

Repository : [pltfrm-web](https://github.com/simongarton/pltfrm-web)

Site : [pltfrm-web](https://pltfrm-web.netlify.app/
)

## Software used

- Java : Corretto 17
- Lombok : for boilerplate code
- Jackson : JSON serialization/deserialization
- Log4J2 : logging library

- Squidfunk : https://github.com/squidfunk/terraform-aws-api-gateway-enable-cors


