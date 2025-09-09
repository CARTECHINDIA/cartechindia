package com.cartechindia.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BiddingResponseDto {
    private Long biddingId;
    private BigDecimal startAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    // CarSelling details
    private Long carId;
    private String regNumber;
    private String brand;
    private String model;
    private String variant;
    private Integer manufactureYear;
    private BigDecimal price;

    // User
    private Long createdBy;
    private String createdByName;
}
