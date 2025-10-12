package com.cartechindia.dto.request;

import com.cartechindia.constraints.BidScheduleStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidScheduleRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long biddingId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long carListingId;

    private BigDecimal basePrice;
    private BigDecimal minIncrement;
    private BidScheduleStatus status;

    private LocalDateTime startTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dailyStartTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dailyEndTime;

    private LocalDateTime endTime;
}
