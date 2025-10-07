package com.cartechindia.dto.request;

import com.cartechindia.constraints.BidScheduleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Schema(description = "Bid schedule details for car listing")
public class BidScheduleRequestDto {

    private Long carListingId;
    private LocalDateTime startTime;
    private LocalDateTime dailyStartTime;
    private LocalDateTime dailyEndTime;
    private LocalDateTime endTime;
    private Double startingBidAmount;
    private Double bidIncrementAmount;
    private BidScheduleStatus status;

}
