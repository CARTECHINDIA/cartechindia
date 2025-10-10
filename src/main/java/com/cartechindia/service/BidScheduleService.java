package com.cartechindia.service;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.dto.request.BidScheduleRequestDto;
import com.cartechindia.dto.request.LiveBidRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.LiveBidResponseDto;

import java.util.List;

public interface BidScheduleService {

    BidScheduleResponseDto scheduleBidding(Long carId, BidScheduleRequestDto dto, String userEmail);
    LiveBidResponseDto placeBid(LiveBidRequestDto dto, String userEmail);
    BidScheduleResponseDto getBiddingDetails(Long biddingId, String userEmail);
    List<BidScheduleResponseDto> getAllBiddings();
}
