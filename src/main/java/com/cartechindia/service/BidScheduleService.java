package com.cartechindia.service;

import com.cartechindia.constraints.BidScheduleStatus;
import com.cartechindia.dto.request.BidScheduleRequestDto;
import com.cartechindia.dto.response.BidScheduleResponseDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.BidSchedule;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.User;
import com.cartechindia.repository.BidScheduleRepository;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BidScheduleService {
    private final BidScheduleRepository bidScheduleRepository;
    private final CarListingRepository carListingRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<BidSchedule> getAll() { return bidScheduleRepository.findAll(); }
    public BidSchedule getById(Long id) { return bidScheduleRepository.findById(id).orElse(null); }

    public BidSchedule save(BidSchedule schedule) {
        // Only dealer can schedule bid
        if (schedule.getDealer() == null) throw new RuntimeException("Dealer must schedule the bid");
        // Auto status based on time
        LocalDateTime now = LocalDateTime.now();
        if (schedule.getStartTime().isBefore(now) && schedule.getEndTime().isAfter(now)) {
            schedule.setStatus(schedule.getStatus() == null ? schedule.getStatus() : schedule.getStatus());
        }
        return bidScheduleRepository.save(schedule);
    }

    public void delete(Long id) { bidScheduleRepository.deleteById(id); }


    public BidScheduleResponseDto createBidSchedule(BidScheduleRequestDto requestDto) {

        // 1. Fetch car listing
        CarListing carListing = carListingRepository.findById(requestDto.getCarListingId())
                .orElseThrow(() -> new RuntimeException("Car listing not found"));

        // 2. For this example, assuming dealer is the current authenticated user
        User dealer = getCurrentUser(); // Implement a method to get authenticated user

        // 3. Map request DTO to entity
        BidSchedule bidSchedule = getBidSchedule(requestDto, carListing, dealer);

        // 4. Save
        BidSchedule saved = bidScheduleRepository.save(bidSchedule);

        // 5. Map entity to response DTO including nested CarListing and CarMasterData
        BidScheduleResponseDto responseDto = modelMapper.map(saved, BidScheduleResponseDto.class);
        responseDto.setCarListing(modelMapper.map(carListing, CarListingResponseDto.class));
        responseDto.getCarListing().setCarMasterData(modelMapper.map(carListing.getCarMasterData(), CarMasterDataResponseDto.class));

        // Optionally set dealer info
        responseDto.setDealerId(dealer.getId());
        responseDto.setDealerName(dealer.getFirstName()+" "+dealer.getLastName());
        responseDto.setCarTitle(carListing.getCarMasterData().getMake() + " " + carListing.getCarMasterData().getModel());

        return responseDto;
    }

    private static BidSchedule getBidSchedule(BidScheduleRequestDto requestDto, CarListing carListing, User dealer) {
        BidSchedule bidSchedule = new BidSchedule();
        bidSchedule.setCarListing(carListing);
        bidSchedule.setDealer(dealer);
        bidSchedule.setStartTime(requestDto.getStartTime());
        bidSchedule.setEndTime(requestDto.getEndTime());
        bidSchedule.setStartingBidAmount(requestDto.getStartingBidAmount());
        bidSchedule.setBidIncrementAmount(requestDto.getBidIncrementAmount());
        bidSchedule.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : BidScheduleStatus.SCHEDULED);
        bidSchedule.setActive(true);
        return bidSchedule;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
