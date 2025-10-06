package com.cartechindia.repository;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.entity.BidSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BidScheduleRepository extends JpaRepository<BidSchedule, Long> {

    List<BidSchedule> findByStatusAndStartTimeBefore(BidScheduleStatus status, LocalDateTime time);

    List<BidSchedule> findByStatusAndEndTimeBefore(BidScheduleStatus status, LocalDateTime time);
}
