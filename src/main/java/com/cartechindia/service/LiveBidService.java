package com.cartechindia.service;

import com.cartechindia.entity.LiveBid;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.User;
import com.cartechindia.repository.LiveBidRepository;
import com.cartechindia.repository.BidScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveBidService {

    private final LiveBidRepository liveBidRepository;
    private final BidScheduleRepository bidScheduleRepository;

    @Transactional
    public LiveBid placeBid(User buyer, BidSchedule schedule, Double bidAmount) {
        LocalDateTime now = LocalDateTime.now();
        if (schedule.getStartTime().isAfter(now) || schedule.getEndTime().isBefore(now)) {
            throw new RuntimeException("Bidding is not active for this schedule");
        }

        if (liveBidRepository.existsByBidScheduleAndBuyerAndIsWinnerFalseAndIsDeletedFalse(schedule, buyer)) {
            throw new RuntimeException("You already have an active bid for this schedule");
        }

        List<LiveBid> bids = liveBidRepository.findByBidScheduleOrderByBidAmountDesc(schedule);
        double highestBid = bids.isEmpty() ? schedule.getStartingBidAmount() : bids.get(0).getBidAmount();
        double minBid = highestBid + schedule.getBidIncrementAmount();
        if (bidAmount < minBid) {
            throw new RuntimeException("Bid must be at least: " + minBid);
        }

        LiveBid liveBid = new LiveBid();
        liveBid.setBidSchedule(schedule);
        liveBid.setCarListing(schedule.getCarListing());
        liveBid.setBuyer(buyer);
        liveBid.setBidAmount(bidAmount);
        liveBid.setBidTime(LocalDateTime.now());
        liveBid.setWinner(false);
        liveBid.setDeleted(false);

        return liveBidRepository.save(liveBid);
    }

    @Transactional
    public LiveBid finalizeWinner(BidSchedule schedule) {
        List<LiveBid> bids = liveBidRepository.findByBidScheduleOrderByBidAmountDesc(schedule);
        if (bids.isEmpty()) return null;

        LiveBid winner = bids.get(0);
        winner.setWinner(true);
        return liveBidRepository.save(winner);
    }

    public List<LiveBid> getActiveBids(BidSchedule schedule) {
        return liveBidRepository.findByBidScheduleAndIsDeletedFalse(schedule);
    }
}
