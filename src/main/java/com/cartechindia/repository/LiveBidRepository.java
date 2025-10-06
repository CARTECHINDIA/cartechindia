// LiveBidRepository.java
package com.cartechindia.repository;
import com.cartechindia.entity.LiveBid;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LiveBidRepository extends JpaRepository<LiveBid, Long> {
    List<LiveBid> findByBidScheduleOrderByBidAmountDesc(BidSchedule schedule);
    boolean existsByBidScheduleAndBuyerAndIsWinnerFalseAndIsDeletedFalse(BidSchedule schedule, User buyer);
    List<LiveBid> findByBidScheduleAndIsDeletedFalse(BidSchedule schedule);
}
