package com.spring.service;

import com.spring.request.GeoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class GeoService {

    @Value("${geo.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;


    public GeoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeoResponse getLatLng(String address) {


        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.opencagedata.com/geocode/v1/json")
                .queryParam("q", address)
                .queryParam("key", apiKey)
                .queryParam("limit", 1)
                .build()
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            throw new RuntimeException("No response from OpenCage API");
        }

        System.out.println("API RESPONSE: " + response);

        Map<String, Object> status = (Map<String, Object>) response.get("status");
        Integer statusCode = (Integer) status.get("code");

        if (statusCode == 402) {
            throw new RuntimeException("API quota exceeded");
        }

        if (statusCode != 200) {
            throw new RuntimeException("OpenCage API error, status code: " + statusCode);
        }

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("results");

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No location found for address: " + address);
        }

        Map<String, Object> geometry =
                (Map<String, Object>) results.get(0).get("geometry");

        GeoResponse geo = new GeoResponse();
        geo.setLat(((Number) geometry.get("lat")).doubleValue());
        geo.setLng(((Number) geometry.get("lng")).doubleValue());

        return geo;
    }
}
