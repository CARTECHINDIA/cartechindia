package com.cartechindia.service;

public interface LoginService {

    public void recordLogin(Long userId, String ipAddress, String deviceInfo,
                            boolean success, Double longitude, Double latitude);
}
