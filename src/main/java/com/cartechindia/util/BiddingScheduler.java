package com.cartechindia.util;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.repository.BidScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BiddingScheduler {

    private final BidScheduleRepository bidScheduleRepository;

    public BiddingScheduler(BidScheduleRepository biddingRepository) {
        this.bidScheduleRepository = biddingRepository;
    }

    // Run every 1 minute to update bidding statuses dynamically
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateBiddingStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<BidSchedule> toUpdate = new ArrayList<>();

        // 1️⃣ Open scheduled biddings if within today's 3-hour window and overall campaign not ended
        List<BidSchedule> scheduledBiddings = bidScheduleRepository.findByStatus(BidScheduleStatus.SCHEDULED);
        for (BidSchedule bidding : scheduledBiddings) {
            LocalDateTime todayStart = bidding.getTodayStartTime();
            LocalDateTime todayEnd = bidding.getTodayEndTime();

            if (!now.isBefore(todayStart) && now.isBefore(todayEnd) &&
                    (bidding.getEndTime() == null || now.isBefore(bidding.getEndTime()))) {
                bidding.setStatus(BidScheduleStatus.SCHEDULED);
                toUpdate.add(bidding);
            }
        }

        // 2️⃣ Close open biddings if daily window is over OR overall campaign ended
        List<BidSchedule> openBiddings = bidScheduleRepository.findByStatus(BidScheduleStatus.SCHEDULED);
        for (BidSchedule bidding : openBiddings) {
            LocalDateTime todayEnd = bidding.getTodayEndTime();

            if (now.isAfter(todayEnd) || (bidding.getEndTime() != null && now.isAfter(bidding.getEndTime()))) {
                bidding.setStatus(BidScheduleStatus.COMPLETED);
                toUpdate.add(bidding);
            }
        }

        // 3️⃣ Save all updated biddings at once
        if (!toUpdate.isEmpty()) {
            bidScheduleRepository.saveAll(toUpdate);
        }
    }
}
