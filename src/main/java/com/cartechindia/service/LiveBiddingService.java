package com.cartechindia.service;

import com.cartechindia.dto.LiveBiddingRequestDto;
import com.cartechindia.dto.LiveBiddingResponseDto;
import java.util.List;

public interface LiveBiddingService {
    LiveBiddingResponseDto placeBid(LiveBiddingRequestDto dto);
    List<LiveBiddingResponseDto> getAllBids(Long biddingId);
    LiveBiddingResponseDto getHighestBid(Long biddingId);
}
