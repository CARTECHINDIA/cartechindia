package com.cartechindia.service;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.dto.request.BidScheduleRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.User;
import com.cartechindia.repository.BidScheduleRepository;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BidScheduleService {

    private final BidScheduleRepository bidScheduleRepository;
    private final CarListingRepository carListingRepository;
    private final AuthUtil authUtil;
    private final MapperService mapperService;

    // ----------------------
    public BidScheduleResponseDto createBidSchedule(BidScheduleRequestDto dto) {
        // 1️⃣ Fetch authenticated dealer
        User dealer = authUtil.getCurrentUser();

        // 2️⃣ Fetch CarListing
        CarListing carListing = carListingRepository.findById(dto.getCarListingId())
                .orElseThrow(() -> new NoSuchElementException("Car listing not found"));

        // 3️⃣ Map DTO → Entity
        BidSchedule schedule = new BidSchedule();
        schedule.setDealer(dealer);
        schedule.setCarListing(carListing);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setStartingBidAmount(dto.getStartingBidAmount());
        schedule.setBidIncrementAmount(dto.getBidIncrementAmount());
        schedule.setStatus(dto.getStatus() != null ? dto.getStatus() : BidScheduleStatus.SCHEDULED);
        schedule.setActive(true);

        // 4️⃣ Save
        BidSchedule saved = bidScheduleRepository.save(schedule);

        // 5️⃣ Map entity → DTO using MapperService for nested CarListing
        BidScheduleResponseDto response = new BidScheduleResponseDto();
        response.setCarListing(mapperService.toCarListingDto(carListing));
        response.setDealerId(dealer.getId());
        response.setDealerName(dealer.getFirstName() + " " + dealer.getLastName());
        response.setCarTitle(carListing.getCarMasterData().getMake() + " " + carListing.getCarMasterData().getModel());

        return response;
    }
}
