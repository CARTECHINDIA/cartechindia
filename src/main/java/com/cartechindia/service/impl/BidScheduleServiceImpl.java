package com.cartechindia.service.impl;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.dto.request.BidScheduleDto;
import com.cartechindia.dto.request.LiveBidRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.LiveBidResponseDto;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.LiveBid;
import com.cartechindia.entity.User;
import com.cartechindia.exception.BiddingNotActiveException;
import com.cartechindia.exception.InvalidBidException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.BidScheduleRepository;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.repository.LiveBidRepository;
import com.cartechindia.repository.UserRepository;
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
    public BidScheduleDto scheduleBidding(Long carId, BidScheduleDto dto, String userEmail) {
        CarListing car = carListingRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BidSchedule bidding = BidSchedule.builder()
                .carListing(car)
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
        dto.setCarId(bidding.getCarListing().getId());
        dto.setCarRegNumber(bidding.getCarListing().getRegNumber());
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
    public List<BidScheduleDto> getAllBiddings() {
        return bidScheduleRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    // ==========================
    // Helper: Map entity → DTO
    // ==========================
    private BidScheduleDto mapToDto(BidSchedule b) {
        BidScheduleDto dto = new BidScheduleDto();
        dto.setBiddingId(b.getBiddingId());
        dto.setCarId(b.getCarListing().getId());
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
