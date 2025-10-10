package com.cartechindia.repository;

import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.LiveBid;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LiveBidRepository extends JpaRepository<LiveBid, Long> {
    List<LiveBid> findByBidScheduleOrderByBidAmountDesc(BidSchedule bidSchedule);

    Optional<LiveBid> findTopByBidScheduleOrderByBidAmountDesc(BidSchedule bidSchedule);
}
