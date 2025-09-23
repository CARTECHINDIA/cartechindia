package com.cartechindia.repository;

import com.cartechindia.entity.Bidding;
import com.cartechindia.entity.BiddingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BiddingRepository extends JpaRepository<Bidding, Long> {
    List<Bidding> findByStatus(BiddingStatus status);
    List<Bidding> findByStatusAndStartTimeBefore(BiddingStatus status, LocalDateTime time);
    List<Bidding> findByStatusAndEndTimeBefore(BiddingStatus status, LocalDateTime time);
}
