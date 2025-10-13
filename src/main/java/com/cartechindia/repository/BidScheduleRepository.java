package com.cartechindia.repository;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.entity.BidSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BidScheduleRepository extends JpaRepository<BidSchedule, Long> {
    List<BidSchedule> findByStatus(BidScheduleStatus status);
    List<BidSchedule> findByStatusAndStartTimeBefore(BidScheduleStatus status, LocalDateTime time);
    List<BidSchedule> findByStatusAndEndTimeBefore(BidScheduleStatus status, LocalDateTime time);
}
