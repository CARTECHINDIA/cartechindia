package com.cartechindia.service;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.repository.BidScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidScheduleStatusUpdater {

    @Autowired
    private BidScheduleRepository bidScheduleRepository;

    public BidScheduleStatusUpdater(BidScheduleRepository bidScheduleRepository) {
        this.bidScheduleRepository = bidScheduleRepository;
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    @Transactional
    public void updateBidScheduleStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<BidSchedule> toActivate = bidScheduleRepository.findByStatusAndStartTimeBefore(BidScheduleStatus.SCHEDULED, now);
        toActivate.forEach(s -> s.setStatus(BidScheduleStatus.ACTIVE));
        bidScheduleRepository.saveAll(toActivate);

        List<BidSchedule> toComplete = bidScheduleRepository.findByStatusAndEndTimeBefore(BidScheduleStatus.ACTIVE, now);
        toComplete.forEach(s -> {
            s.setStatus(BidScheduleStatus.COMPLETED);
            s.setActive(false);
        });
        bidScheduleRepository.saveAll(toComplete);
    }
}
