package com.cartechindia.service.impl;

import com.cartechindia.dto.BiddingDto;
import com.cartechindia.dto.BiddingResponseDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.entity.*;
import com.cartechindia.repository.BiddingRepository;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.BiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BiddingServiceImpl implements BiddingService {

    private final BiddingRepository biddingRepository;
    private final CarSellingRepository carRepository;
    private final UserRepository userRepository;

    // Create new bidding
    @Override
    public Bidding createBidding(BiddingDto dto, String email) {
        CarSelling car = carRepository.findById(dto.getCarSellingId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bidding bidding = Bidding.builder()
                .carSelling(car)
                .startAmount(dto.getStartAmount())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(dto.getStatus() != null ? dto.getStatus() : BiddingStatus.SCHEDULED)
                .createdBy(user)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .updatedBy(null)
                .build();

        return biddingRepository.save(bidding);
    }



    @Override
    public PageResponse<BiddingResponseDto> getAllBiddings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Bidding> biddingPage = biddingRepository.findAll(pageable);

        List<BiddingResponseDto> content = biddingPage.getContent().stream().map(bidding -> {
            BiddingResponseDto dto = new BiddingResponseDto();
            dto.setBiddingId(bidding.getBiddingId());
            dto.setStartAmount(bidding.getStartAmount());
            dto.setStartTime(bidding.getStartTime());
            dto.setEndTime(bidding.getEndTime());
            dto.setStatus(bidding.getStatus().name());

            // Car details
            CarSelling car = bidding.getCarSelling();
            dto.setRegNumber(car.getRegNumber());
            dto.setManufactureYear(car.getManufactureYear());
            dto.setPrice(car.getPrice());

            // User details
            dto.setCreatedBy(bidding.getCreatedBy().getId());
            dto.setCreatedByName(bidding.getCreatedBy().getFirstName()+" "+bidding.getCreatedBy().getLastName());

            return dto;
        }).toList();

        return new PageResponse<>(
                content,
                biddingPage.getNumber(),
                biddingPage.getSize(),
                biddingPage.getTotalElements(),
                biddingPage.getTotalPages(),
                biddingPage.isFirst(),
                biddingPage.isLast()
        );
    }
}
