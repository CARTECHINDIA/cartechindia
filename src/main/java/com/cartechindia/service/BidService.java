package com.cartechindia.service;

import com.cartechindia.entity.Bid;
import com.cartechindia.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface BidService {

    Bid placeBid(Long biddingId, User user, BigDecimal amount);

    List<Bid> getBidsForBidding(Long biddingId);

    Bid getHighestBid(Long biddingId);
}
