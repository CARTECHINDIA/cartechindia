package com.cartechindia.serviceImpl;

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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BiddingServiceImpl implements BiddingService {

    private final BiddingRepository biddingRepository;
    private final CarSellingRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public Bidding createBidding(BiddingDto dto) {
        CarSelling car = carRepository.findBySellingId(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        User createdBy = userRepository.findById(dto.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bidding bidding = new Bidding();
        bidding.setCarSelling(car);
        bidding.setStartAmount(dto.getStartAmount());
        bidding.setStartTime(dto.getStartTime());
        bidding.setEndTime(dto.getEndTime());
        bidding.setCreatedBy(createdBy);

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
            dto.setCarId(car.getSellingId());
            dto.setRegNumber(car.getRegNumber());
            dto.setBrand(car.getBrand());
            dto.setModel(car.getModel());
            dto.setVariant(car.getVariant());
            dto.setManufactureYear(car.getManufactureYear());
            dto.setPrice(car.getPrice());

            // User details
            dto.setCreatedBy(bidding.getCreatedBy().getId());
            dto.setCreatedByName(bidding.getCreatedBy().getFullName());

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
