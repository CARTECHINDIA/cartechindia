package com.cartechindia.service.impl;

import com.cartechindia.dto.*;
import com.cartechindia.entity.*;
import com.cartechindia.exception.BiddingNotActiveException;
import com.cartechindia.exception.InvalidBidException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.*;
import com.cartechindia.service.BiddingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BiddingServiceImpl implements BiddingService {

    private final BiddingRepository biddingRepository;
    private final BidRepository bidRepository;
    private final CarSellingRepository carSellingRepository;
    private final UserRepository userRepository;

    @Override
    public BiddingDto scheduleBidding(Long carId, BiddingDto dto, String userEmail) {
        CarSelling car = carSellingRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Bidding bidding = Bidding.builder()
                .carSelling(car)
                .basePrice(dto.getBasePrice())
                .minIncrement(dto.getMinIncrement() != null ? dto.getMinIncrement() : BigDecimal.valueOf(1000))
                .startTime(dto.getStartTime())      // user-provided first start time
                .endTime(dto.getEndTime())          // user-provided overall end time
                .status(BiddingStatus.SCHEDULED)
                .createdBy(creator)
                .build();

        Bidding saved = biddingRepository.save(bidding);
        return mapToDto(saved);
    }

    @Override
    public BidResponseDto placeBid(BidRequestDto dto, String userEmail) {
        Bidding bidding = biddingRepository.findById(dto.getBiddingId())
                .orElseThrow(() -> new ResourceNotFoundException("Bidding not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = bidding.getTodayStartTime();
        LocalDateTime todayEnd = bidding.getTodayEndTime();

        if (now.isBefore(todayStart) || now.isAfter(todayEnd) || now.isAfter(bidding.getEndTime())) {
            throw new BiddingNotActiveException("Bidding is not active at this time");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Bid> existing = bidRepository.findByBiddingOrderByBidAmountDesc(bidding);
        BigDecimal minAccept = existing.isEmpty()
                ? bidding.getBasePrice()
                : existing.get(0).getBidAmount().add(bidding.getMinIncrement());

        if (dto.getBidAmount().compareTo(minAccept) < 0) {
            throw new InvalidBidException("Bid must be at least %s".formatted(minAccept));
        }

        Bid bid = Bid.builder()
                .bidding(bidding)
                .user(user)
                .bidAmount(dto.getBidAmount())
                .bidTime(now)
                .build();

        bidRepository.save(bid);

        BidResponseDto res = new BidResponseDto();
        res.setBidderName("%s %s".formatted(user.getFirstName(), user.getLastName() != null ? user.getLastName() : ""));
        res.setBidAmount(bid.getBidAmount());
        res.setBidTime(bid.getBidTime());
        res.setWinner(false);

        return res;
    }

    @Override
    public BiddingResponseDto getBiddingDetails(Long biddingId, String userEmail) {
        Bidding bidding = biddingRepository.findById(biddingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bidding not found"));

        List<Bid> bids = bidRepository.findByBiddingOrderByBidAmountDesc(bidding);
        boolean isParticipant = bids.stream().anyMatch(b -> b.getUser().getEmail().equalsIgnoreCase(userEmail));

        BiddingResponseDto dto = new BiddingResponseDto();
        dto.setBiddingId(bidding.getBiddingId());
        dto.setCarId(bidding.getCarSelling().getId());
        dto.setCarRegNumber(bidding.getCarSelling().getRegNumber());
        dto.setBasePrice(bidding.getBasePrice());
        dto.setStatus(bidding.getStatus());

        dto.setDailyStartTime(bidding.getTodayStartTime());
        dto.setDailyEndTime(bidding.getTodayEndTime());
        dto.setEndTime(bidding.getEndTime());
        dto.setHighestBid(bids.isEmpty() ? bidding.getBasePrice() : bids.get(0).getBidAmount());

        List<BidResponseDto> bidList = bids.stream().map(b -> {
            BidResponseDto r = new BidResponseDto();
            if (isParticipant) r.setBidderName("%s %s".formatted(
                    b.getUser().getFirstName(),
                    b.getUser().getLastName() != null ? b.getUser().getLastName() : b.getUser().getEmail()
            ));
            else r.setBidderName("Hidden");
            r.setBidAmount(b.getBidAmount());
            r.setBidTime(b.getBidTime());
            r.setWinner(b.getUser().equals(bidding.getWinner()));
            return r;
        }).toList();

        dto.setBids(bidList);
        return dto;
    }

    @Override
    public List<BiddingDto> getAllBiddings() {
        return biddingRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    // ==========================
    // Helper: Map entity → DTO
    // ==========================
    private BiddingDto mapToDto(Bidding b) {
        BiddingDto dto = new BiddingDto();
        dto.setBiddingId(b.getBiddingId());
        dto.setCarId(b.getCarSelling().getId());
        dto.setBasePrice(b.getBasePrice());
        dto.setMinIncrement(b.getMinIncrement());
        dto.setStatus(b.getStatus());

        // Daily 3-hour bidding window (read-only)
        dto.setDailyStartTime(b.getTodayStartTime());
        dto.setDailyEndTime(b.getTodayEndTime());

        // Campaign start and overall end times
        dto.setStartTime(b.getStartTime());
        dto.setEndTime(b.getEndTime());

        return dto;
    }
}
