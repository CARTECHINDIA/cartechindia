package com.cartechindia.dto.request;

import com.cartechindia.constraints.BidScheduleStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class BidScheduleRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long biddingId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long carId;

    private BigDecimal basePrice;
    private BigDecimal minIncrement;
    private BidScheduleStatus status;

    // Input: the campaign's first start time provided by user
    private LocalDateTime startTime;

    // Computed: Today's 3-hour window (read-only)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dailyStartTime;  // today's window start

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dailyEndTime;

    // Overall campaign end time (read/write)
    private LocalDateTime endTime;
}
