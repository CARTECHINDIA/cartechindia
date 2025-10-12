package com.cartechindia.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LiveBidResponseDto {
    private Long bidId;
    private BigDecimal amount;
    private LocalDateTime bidTime;

    private Long buyerId;
    private String buyerName;
    private String buyerEmail;

    private Long carId;
    private String regNumber;
    private String make;
    private String model;
    private String variant;
    private BigDecimal price;

    private boolean isWinner;
}
