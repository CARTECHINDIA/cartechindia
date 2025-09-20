package com.cartechindia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinnerResponseDto {
    private Long biddingId;
    private String carRegNumber;
    private String winnerName;
    private BigDecimal winningAmount;
}
