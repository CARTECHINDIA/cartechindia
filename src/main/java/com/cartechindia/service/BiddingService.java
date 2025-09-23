package com.cartechindia.service;

import com.cartechindia.dto.*;

import java.util.List;

public interface BiddingService {
    BiddingDto scheduleBidding(Long carId, BiddingDto dto, String userEmail);
    BidResponseDto placeBid(BidRequestDto dto, String userEmail);
    BiddingResponseDto getBiddingDetails(Long biddingId, String userEmail);
    List<BiddingDto> getAllBiddings();
}
