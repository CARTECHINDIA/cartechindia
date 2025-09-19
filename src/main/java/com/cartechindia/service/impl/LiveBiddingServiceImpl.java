package com.cartechindia.service.impl;

import com.cartechindia.dto.LiveBiddingRequestDto;
import com.cartechindia.dto.LiveBiddingResponseDto;
import com.cartechindia.entity.Bidding;
import com.cartechindia.entity.LiveBidding;
import com.cartechindia.entity.User;
import com.cartechindia.repository.BiddingRepository;
import com.cartechindia.repository.LiveBiddingRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.LiveBiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveBiddingServiceImpl implements LiveBiddingService {

    private final LiveBiddingRepository liveBiddingRepository;
    private final BiddingRepository biddingRepository;
    private final UserRepository userRepository;

    @Override
    public LiveBiddingResponseDto placeBid(LiveBiddingRequestDto dto) {
        Bidding bidding = biddingRepository.findById(dto.getBiddingId())
                .orElseThrow(() -> new RuntimeException("Bidding not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User createdBy = userRepository.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("CreatedBy not found"));

        LiveBidding liveBidding = new LiveBidding();
        liveBidding.setBidding(bidding);
        liveBidding.setUser(user);
        liveBidding.setBidAmount(dto.getBidAmount());
        liveBidding.setCreatedBy(createdBy);
        liveBidding.setBidTime(LocalDateTime.now());
        liveBidding.setCreatedDate(LocalDateTime.now());
        liveBidding.setUpdatedDate(LocalDateTime.now());

        LiveBidding saved = liveBiddingRepository.save(liveBidding);

        return mapToResponse(saved);
    }

    @Override
    public List<LiveBiddingResponseDto> getAllBids(Long biddingId) {
        return liveBiddingRepository.findByBidding_BiddingIdOrderByBidAmountDesc(biddingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LiveBiddingResponseDto getHighestBid(Long biddingId) {
        LiveBidding highest = liveBiddingRepository.findTopByBidding_BiddingIdOrderByBidAmountDesc(biddingId)
                .orElseThrow(() -> new RuntimeException("No bids yet"));
        return mapToResponse(highest);
    }

    private LiveBiddingResponseDto mapToResponse(LiveBidding liveBidding) {
        LiveBiddingResponseDto dto = new LiveBiddingResponseDto();
        dto.setLiveBidId(liveBidding.getLiveBidId());
        dto.setBiddingId(liveBidding.getBidding().getBiddingId());
        dto.setUserId(liveBidding.getUser().getId());
        dto.setBidAmount(liveBidding.getBidAmount());
        dto.setBidTime(liveBidding.getBidTime());
        return dto;
    }
}
