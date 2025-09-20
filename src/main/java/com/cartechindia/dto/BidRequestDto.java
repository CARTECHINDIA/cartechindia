package com.cartechindia.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BidRequestDto {
    private Long biddingId;
    private BigDecimal bidAmount;
}
