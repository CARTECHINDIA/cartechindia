package com.cartechindia.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CarSellingProjection {
    Long getId();
    String getRegNumber();
    Long getCarId();
    Integer getManufactureYear();
    Integer getKmDriven();
    String getColor();
    Integer getOwners();
    BigDecimal getPrice();
    String getHealth();        // ✅ matches entity
    String getInsurance();
    LocalDate getRegistrationDate();
    String getState();
    String getCity();
    String getStatus();
    LocalDateTime getCreatedAt();

    // Joined from CARS
    String getBrand();
    String getModel();
    String getVariant();
    String getFuelType();
    String getTransmission();
    String getBodyType();

}
