package com.cartechindia.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidResponseDto {
    private String bidderName;   // "Hidden" for non-participant
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private boolean winner;
}
