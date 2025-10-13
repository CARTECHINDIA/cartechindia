package com.cartechindia.service;

import com.cartechindia.entity.LiveBid;
import com.cartechindia.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface LiveBidService {

    LiveBid placeBid(Long biddingId, User user, BigDecimal amount);

    List<LiveBid> getBidsForBidding(Long biddingId);

    LiveBid getHighestBid(Long biddingId);
}
