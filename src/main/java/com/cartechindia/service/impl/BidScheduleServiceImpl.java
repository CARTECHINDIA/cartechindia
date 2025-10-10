package com.cartechindia.service.impl;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.dto.request.BidScheduleRequestDto;
import com.cartechindia.dto.request.LiveBidRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.LiveBidResponseDto;
import com.cartechindia.entity.*;
import com.cartechindia.exception.BiddingNotActiveException;
import com.cartechindia.exception.InvalidBidException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.*;
import com.cartechindia.service.BidScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BidScheduleServiceImpl implements BidScheduleService {

    private final BidScheduleRepository bidScheduleRepository;
    private final LiveBidRepository liveBidRepository;
    private final CarListingRepository carListingRepository;
    private final UserRepository userRepository;

    @Override
    public BidScheduleResponseDto scheduleBidding(Long carId, BidScheduleRequestDto dto, String userEmail) {
        CarListing car = carListingRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BidSchedule bidding = BidSchedule.builder()
                .carSelling(car)
                .basePrice(dto.getBasePrice())
                .minIncrement(dto.getMinIncrement() != null ? dto.getMinIncrement() : BigDecimal.valueOf(1000))
                .startTime(dto.getStartTime())      // user-provided first start time
                .endTime(dto.getEndTime())          // user-provided overall end time
                .status(BidScheduleStatus.SCHEDULED)
                .createdBy(creator)
                .build();

        BidSchedule saved = bidScheduleRepository.save(bidding);
        return mapToDto(saved);
    }

    @Override
    public LiveBidResponseDto placeBid(LiveBidRequestDto dto, String userEmail) {
        BidSchedule bidding = bidScheduleRepository.findById(dto.getBiddingId())
                .orElseThrow(() -> new ResourceNotFoundException("Bidding not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = bidding.getTodayStartTime();
        LocalDateTime todayEnd = bidding.getTodayEndTime();

        if (now.isBefore(todayStart) || now.isAfter(todayEnd) || now.isAfter(bidding.getEndTime())) {
            throw new BiddingNotActiveException("Bidding is not active at this time");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<LiveBid> existing = liveBidRepository.findByBidScheduleOrderByBidAmountDesc(bidding);
        BigDecimal minAccept = existing.isEmpty()
                ? bidding.getBasePrice()
                : existing.get(0).getBidAmount().add(bidding.getMinIncrement());

        if (dto.getBidAmount().compareTo(minAccept) < 0) {
            throw new InvalidBidException("Bid must be at least %s".formatted(minAccept));
        }

        LiveBid bid = LiveBid.builder()
                .bidSchedule(bidding)
                .user(user)
                .bidAmount(dto.getBidAmount())
                .bidTime(now)
                .build();

        liveBidRepository.save(bid);

        LiveBidResponseDto res = new LiveBidResponseDto();
        res.setBidderName("%s %s".formatted(user.getFirstName(), user.getLastName() != null ? user.getLastName() : ""));
        res.setBidAmount(bid.getBidAmount());
        res.setBidTime(bid.getBidTime());
        res.setWinner(false);

        return res;
    }

    @Override
    public BidScheduleResponseDto getBiddingDetails(Long biddingId, String userEmail) {
        BidSchedule bidding = bidScheduleRepository.findById(biddingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bidding not found"));

        List<LiveBid> bids = liveBidRepository.findByBidScheduleOrderByBidAmountDesc(bidding);
        boolean isParticipant = bids.stream().anyMatch(b -> b.getUser().getEmail().equalsIgnoreCase(userEmail));

        BidScheduleResponseDto dto = new BidScheduleResponseDto();
        dto.setBiddingId(bidding.getBiddingId());
        dto.setCarId(bidding.getCarSelling().getId());
        dto.setCarRegNumber(bidding.getCarSelling().getRegNumber());
        dto.setBasePrice(bidding.getBasePrice());
        dto.setStatus(bidding.getStatus());

        dto.setDailyStartTime(bidding.getTodayStartTime());
        dto.setDailyEndTime(bidding.getTodayEndTime());
        dto.setEndTime(bidding.getEndTime());
        dto.setHighestBid(bids.isEmpty() ? bidding.getBasePrice() : bids.get(0).getBidAmount());

        List<LiveBidResponseDto> bidList = bids.stream().map(b -> {
            LiveBidResponseDto r = new LiveBidResponseDto();
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
    public List<BidScheduleResponseDto> getAllBiddings() {
        return bidScheduleRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    // ==========================
    // Helper: Map entity → DTO
    // ==========================
    private BidScheduleResponseDto mapToDto(BidSchedule b) {
        BidScheduleResponseDto dto = new BidScheduleResponseDto();
        dto.setBiddingId(b.getBiddingId());
        dto.setCarId(b.getCarSelling().getId());
        dto.setBasePrice(b.getBasePrice());
        dto.setBasePrice(b.getMinIncrement());
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
