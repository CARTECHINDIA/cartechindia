//package com.cartechindia.service.impl;
//
//import com.cartechindia.entity.Bid;
//import com.cartechindia.entity.Bidding;
//import com.cartechindia.entity.User;
//import com.cartechindia.repository.BidRepository;
//import com.cartechindia.repository.BiddingRepository;
//import com.cartechindia.service.BidService;
//import com.cartechindia.entity.BiddingStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class BidServiceImpl implements BidService {
//
//    private final BidRepository bidRepository;
//    private final BiddingRepository biddingRepository;
//
//    @Override
//    public Bid placeBid(Long biddingId, User user, BigDecimal amount) {
//        Bidding bidding = biddingRepository.findById(biddingId)
//                .orElseThrow(() -> new RuntimeException("Bidding not found"));
//
//        // 1️⃣ Check bidding status
//        if (!BiddingStatus.OPEN.equals(bidding.getStatus()) &&
//                !"Open".equalsIgnoreCase(bidding.getStatus().name())) {
//            throw new RuntimeException("Bidding is not active");
//        }
//
//        // 2️⃣ Base price check
//        if (amount.compareTo(bidding.getBasePrice()) < 0) {
//            throw new RuntimeException("Bid amount must be >= base price");
//        }
//
//        // 3️⃣ Must be greater than last highest bid
//        Bid highestBid = getHighestBid(biddingId);
//        if (highestBid != null && amount.compareTo(highestBid.getBidAmount()) <= 0) {
//            throw new RuntimeException("Bid amount must be greater than last bid");
//        }
//
//        // 4️⃣ Save bid
//        Bid bid = new Bid();
//        bid.setBidding(bidding);
//        bid.setUser(user);
//        bid.setBidAmount(amount);
//        return bidRepository.save(bid);
//    }
//
//
//    @Override
//    public List<Bid> getBidsForBidding(Long biddingId) {
//        Bidding bidding = biddingRepository.findById(biddingId)
//                .orElseThrow(() -> new RuntimeException("Bidding not found"));
//        return bidRepository.findByBiddingOrderByBidAmountDesc(bidding);
//    }
//
//    @Override
//    public Bid getHighestBid(Long biddingId) {
//        Bidding bidding = biddingRepository.findById(biddingId)
//                .orElseThrow(() -> new RuntimeException("Bidding not found"));
//        return bidRepository.findTopByBiddingOrderByBidAmountDesc(bidding).orElse(null);
//    }
//}
