package com.cartechindia.service.impl;

import com.cartechindia.entity.Login;
import com.cartechindia.repository.LoginRepository;
import com.cartechindia.service.LoginService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepository;

    public LoginServiceImpl(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    public void recordLogin(Long userId, String ipAddress, String deviceInfo,
                            boolean success, Double longitude, Double latitude) {

        Login history = Login.builder()
                .userId(userId)
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .deviceInfo(deviceInfo)
                .success(success)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        loginRepository.save(history);
    }

    public List<Login> getUserHistory(Long userId) {
        return loginRepository.findByUserId(userId);
    }

    public Login getLastLogin(Long userId) {
        return loginRepository.findTop1ByUserIdOrderByLoginTimeDesc(userId);
    }
}
