package com.cartechindia.dto;

import com.cartechindia.entity.BiddingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BiddingResponseDto {

    private Long biddingId;
    private Long carId;
    private String carRegNumber;
    private BigDecimal basePrice;
    private BigDecimal highestBid;
    private BiddingStatus status;

    // Daily 3-hour bidding window (today)
    private LocalDateTime dailyStartTime;
    private LocalDateTime dailyEndTime;

    // Overall campaign end time
    private LocalDateTime endTime;

    private LocalDateTime startTime;

    // Full bid list (names hidden for non-participants)
    private List<BidResponseDto> bids;
}
