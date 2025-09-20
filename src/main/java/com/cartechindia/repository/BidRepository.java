package com.cartechindia.repository;

import com.cartechindia.entity.Bid;
import com.cartechindia.entity.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    // find all bids for a bidding ordered by amount desc
    List<Bid> findByBiddingOrderByBidAmountDesc(Bidding bidding);

    // top bid
    Optional<Bid> findTopByBiddingOrderByBidAmountDesc(Bidding bidding);
}
