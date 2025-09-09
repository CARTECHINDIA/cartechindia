package com.cartechindia.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BiddingDto {
    private Long carId;
    private BigDecimal startAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createdBy;
}
