package com.cartechindia.repository;

import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.LiveBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveBidRepository extends JpaRepository<LiveBid, Long> {

    // Find all bids for a given BidSchedule ordered by bid amount descending
    List<LiveBid> findByBidScheduleOrderByBidAmountDesc(BidSchedule bidSchedule);

    // Find the top (highest) bid for a given BidSchedule
    Optional<LiveBid> findTopByBidScheduleOrderByBidAmountDesc(BidSchedule bidSchedule);
}
