package com.cartechindia.repository;

import com.cartechindia.entity.LiveBidding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiveBiddingRepository extends JpaRepository<LiveBidding, Long> {

    // Get all bids for a specific bidding
    List<LiveBidding> findByBidding_BiddingIdOrderByBidAmountDesc(Long biddingId);

    // Get highest bid for a bidding
    Optional<LiveBidding> findTopByBidding_BiddingIdOrderByBidAmountDesc(Long biddingId);
}
