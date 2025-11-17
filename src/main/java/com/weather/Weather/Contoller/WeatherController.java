package com.weather.Weather.Contoller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.key}")
    private String apiKey;

    @GetMapping("/{city}")
    public ResponseEntity<?> getWeather(@PathVariable String city) {
        try {

            String fullApiUrl = String.format("%s?key=%s&q=%s&aqi=yes", apiUrl, apiKey, city);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(fullApiUrl, Map.class);

            Map<String, Object> location = (Map<String, Object>) response.get("location");
            Map<String, Object> current = (Map<String, Object>) response.get("current");

            Map<String, Object> result = Map.of(
                    "city", location.get("name"),
                    "country", location.get("country"),
                    "temperature", current.get("temp_c"),
                    "feelsLike", current.get("feelslike_c"),
                    "condition", ((Map<String, Object>) current.get("condition")).get("text")
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "City not found or API error!"));
        }
    }
}