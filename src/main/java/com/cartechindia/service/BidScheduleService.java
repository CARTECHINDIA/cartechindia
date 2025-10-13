package com.cartechindia.service;

import com.cartechindia.dto.request.BidScheduleDto;
import com.cartechindia.dto.request.LiveBidRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.LiveBidResponseDto;

import java.util.List;

public interface BidScheduleService {
    BidScheduleDto scheduleBidding(Long carId, BidScheduleDto dto, String userEmail);
    LiveBidResponseDto placeBid(LiveBidRequestDto dto, String userEmail);
    BidScheduleResponseDto getBiddingDetails(Long biddingId, String userEmail);
    List<BidScheduleDto> getAllBiddings();
}
