/*
package com.cartechindia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarBidService {

    @Autowired
    private CarBidRepository carBidRepository;

    @Autowired
    private BidScheduleRepository bidScheduleRepository;

    @Transactional
    public CarBid placeBid(User buyer, Long carListingId, Double bidAmount) {
        // Fetch active bid schedule
        BidSchedule schedule = bidScheduleRepository
                .findByCarListingIdAndStatus(carListingId, BidScheduleStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active bidding for this car"));

        // Check starting bid and increment
        Double highestBid = carBidRepository.findHighestBidAmountByCarListing(carListingId).orElse(0.0);
        double minimumAllowed = Math.max(schedule.getStartingBidAmount(), highestBid + schedule.getBidIncrementAmount());

        if (bidAmount < minimumAllowed) {
            throw new RuntimeException("Bid must be at least " + minimumAllowed);
        }

        // Check if user already has active bid
        boolean hasActiveBid = carBidRepository.existsByCarListingIdAndBuyerIdAndStatus(carListingId, buyer.getId(), BidStatus.PENDING);
        if (hasActiveBid) {
            throw new RuntimeException("You already have an active bid for this listing");
        }

        // Save bid
        CarBid bid = new CarBid();
        bid.setBuyer(buyer);
        bid.setCarListing(schedule.getCarListing());
        bid.setBidAmount(bidAmount);
        bid.setBidTime(LocalDateTime.now());
        bid.setStatus(BidStatus.PENDING);

        return carBidRepository.save(bid);
    }
}
*/
