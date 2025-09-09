package com.cartechindia.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LiveBiddingRequestDto {
    private Long biddingId;
    private Long userId;
    private BigDecimal bidAmount;
    private Long createdBy;
}