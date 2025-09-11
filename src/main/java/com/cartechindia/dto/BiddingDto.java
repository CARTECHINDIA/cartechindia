package com.cartechindia.dto;

import com.cartechindia.entity.BiddingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BiddingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long biddingId;

    private Long carSellingId;

    private BigDecimal startAmount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BiddingStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long createdById;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long updatedById;
}
