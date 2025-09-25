package com.cartechindia.util;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CarsProjection {
    Long getCarId();
    String getBrand();
    String getModel();
    String getVariant();
    String getFuelType();
    String getTransmission();
    String getDrivetrain();
    String getBodyType();
    String getSegment();
    Integer getSeatingCapacity();
    Integer getSafetyRatingStars();
    Integer getLaunchYear();
    Integer getEngineCc();
    Integer getPowerBhp();
    Integer getTorqueNm();
    Integer getLengthMm();
    Integer getWidthMm();
    Integer getHeightMm();
    Integer getWheelbaseMm();
    Integer getGroundClearanceMm();
    BigDecimal getBatteryKwh();
    BigDecimal getExShowroomPriceLakh();
    BigDecimal getEfficiency();
    String getEfficiencyUnit();
    String getColorOptions();
    String getMarketStatus();
    String getBs6Phase();
    LocalDate getUpdatedDate();
    String getUpdatedBy();
    String getCreatedBy();
}
