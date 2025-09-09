package com.cartechindia.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LiveBiddingResponseDto {
    private Long liveBidId;
    private Long biddingId;
    private Long userId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
}