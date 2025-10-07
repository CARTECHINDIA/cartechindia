package com.cartechindia.dto.response;

import com.cartechindia.constraints.BidScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.html.HTMLAnchorElement;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
public class BidScheduleResponseDto {

    private Long id;
    private Long carListingId;
    private String carTitle;
    private Long dealerId;
    private String dealerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startingBidAmount;
    private Double bidIncrementAmount;
    private Double maxBidAmount;
    private Integer autoExtendMinutes;
    private boolean isActive;
    private BidScheduleStatus status;
    private CarListingResponseDto carListing = new CarListingResponseDto(); // initialize
}
