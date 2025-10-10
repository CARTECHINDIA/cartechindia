package com.cartechindia.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LiveBidRequestDto {

    private Long biddingId;
    private BigDecimal bidAmount;
}
