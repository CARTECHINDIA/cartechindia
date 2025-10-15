package com.cartechindia.service.impl;

import com.cartechindia.exception.ExternalApiException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GoogleMapsClient {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleMapsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DistanceResult getDistanceAndDuration(double originLat, double originLng,
                                                 double destLat, double destLng,
                                                 String mode) {
        try {
            String origins = originLat + "," + originLng;
            String destinations = destLat + "," + destLng;

            String encodedOrigins = URLEncoder.encode(origins, StandardCharsets.UTF_8);
            String encodedDest = URLEncoder.encode(destinations, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&mode=%s&units=metric&key=%s",
                    encodedOrigins, encodedDest, mode, apiKey
            );

            Map response = restTemplate.getForObject(url, Map.class);
            if (response == null) throw new RuntimeException("Empty response from Google");

            var rows = (java.util.List) response.get("rows");
            var elements = (java.util.List) ((Map) rows.get(0)).get("elements");
            var element = (Map) elements.get(0);

            Map distance = (Map) element.get("distance");
            Map duration = (Map) element.get("duration");

            DistanceResult result = new DistanceResult();
            if (distance != null) {
                result.setDistanceText((String) distance.get("text"));
                Number meters = (Number) distance.get("value");
                result.setDistanceInMeters(Double.valueOf(meters != null ? meters.longValue() : null));
            }
            if (duration != null) {
                result.setDurationText((String) duration.get("text"));
            }

            return result;
        } catch (Exception ex) {
            throw new ExternalApiException("Failed to call Google Distance Matrix API", ex);
        }
    }

    @Setter
    @Getter
    public static class DistanceResult {
        private Double distanceInMeters;
        @Getter
        private String distanceText;
        @Getter
        private String durationText;
    }
}
