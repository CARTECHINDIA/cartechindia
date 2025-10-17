package com.cartechindia.util;

public class DistanceCalculator {

    // Calculate distance in kilometers between two lat/lon points
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // Optional: estimate travel time (assuming avg speed in km/h)
    public static double estimateTravelTimeMin(double distanceKm, double avgSpeedKmh) {
        return (distanceKm / avgSpeedKmh) * 60; // minutes
    }
}
