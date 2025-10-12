package com.cartechindia.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidScheduleResponseDto {
    private Long id;
    private CarListingResponseDto carListing;
    private Long dealerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal startingBidAmount;
    private BigDecimal bidIncrementAmount;
    private BigDecimal maxBidAmount;
    private Integer autoExtendMinutes;
    private boolean isActive;
    private String status;

    // Winner info
    private Long winningBidId;
    private BigDecimal winningBidAmount;
    private Long winnerId;
    private String winnerName;
}
