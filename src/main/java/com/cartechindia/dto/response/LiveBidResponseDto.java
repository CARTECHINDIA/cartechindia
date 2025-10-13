package com.cartechindia.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LiveBidResponseDto {
    private String bidderName;   // "Hidden" for non-participant
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private boolean winner;
}
