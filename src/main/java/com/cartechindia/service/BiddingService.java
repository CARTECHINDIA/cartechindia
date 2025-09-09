package com.cartechindia.service;

import com.cartechindia.dto.BiddingDto;
import com.cartechindia.dto.BiddingResponseDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.entity.Bidding;

import java.util.List;

public interface BiddingService {
    Bidding createBidding(BiddingDto dto);

    PageResponse<BiddingResponseDto> getAllBiddings(int page, int size);



}
