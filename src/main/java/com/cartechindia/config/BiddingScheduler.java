package com.cartechindia.config;

import com.cartechindia.entity.Bidding;
import com.cartechindia.entity.BiddingStatus;
import com.cartechindia.repository.BiddingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BiddingScheduler {

    private final BiddingRepository biddingRepository;

    public BiddingScheduler(BiddingRepository biddingRepository) {
        this.biddingRepository = biddingRepository;
    }

    // Run every 1 minute to update bidding statuses dynamically
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateBiddingStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Bidding> toUpdate = new ArrayList<>();

        // 1️⃣ Open scheduled biddings if within today's 3-hour window and overall campaign not ended
        List<Bidding> scheduledBiddings = biddingRepository.findByStatus(BiddingStatus.SCHEDULED);
        for (Bidding bidding : scheduledBiddings) {
            LocalDateTime todayStart = bidding.getTodayStartTime();
            LocalDateTime todayEnd = bidding.getTodayEndTime();

            if (!now.isBefore(todayStart) && now.isBefore(todayEnd) &&
                    (bidding.getEndTime() == null || now.isBefore(bidding.getEndTime()))) {
                bidding.setStatus(BiddingStatus.OPEN);
                toUpdate.add(bidding);
            }
        }

        // 2️⃣ Close open biddings if daily window is over OR overall campaign ended
        List<Bidding> openBiddings = biddingRepository.findByStatus(BiddingStatus.OPEN);
        for (Bidding bidding : openBiddings) {
            LocalDateTime todayEnd = bidding.getTodayEndTime();

            if (now.isAfter(todayEnd) || (bidding.getEndTime() != null && now.isAfter(bidding.getEndTime()))) {
                bidding.setStatus(BiddingStatus.COMPLETED);
                toUpdate.add(bidding);
            }
        }

        // 3️⃣ Save all updated biddings at once
        if (!toUpdate.isEmpty()) {
            biddingRepository.saveAll(toUpdate);
        }
    }
}
