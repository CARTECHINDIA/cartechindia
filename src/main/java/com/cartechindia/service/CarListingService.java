package com.cartechindia.service;


import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.*;
import com.cartechindia.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarListingService {
    private final CarListingRepository carListingRepository;
    private final CarMasterDataRepository carMasterDataRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RTORegistrationRepository rtoRegistrationRepository;
    private final ModelMapper modelMapper;

    public List<CarListing> getAll() { return carListingRepository.findAll(); }
    public CarListing getById(Long id) { return carListingRepository.findById(id).orElse(null); }

    public CarListing save(CarListing car) {
        if (!car.isDeleted()) car.setDeleted(false);
        return carListingRepository.save(car);
    }

    public void delete(Long id) {
        CarListing car = getById(id);
        if (car != null) {
            car.setDeleted(true);
            carListingRepository.save(car);
        }
    }

    //==========================================

    public CarListingResponseDto createCarListing(CarListingRequestDto dto) {
        // 1️⃣ Fetch authenticated user (seller)
        User seller = getCurrentUser();

        // 2️⃣ Fetch required nested entities
        CarMasterData carMasterData = carMasterDataRepository.findById(dto.getCarMasterId())
                .orElseThrow(() -> new NoSuchElementException("Car master data not found"));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new NoSuchElementException("Location not found"));

        RtoRegistration rtoRegistration = rtoRegistrationRepository.findById(dto.getRtoRegistrationId())
                .orElseThrow(() -> new NoSuchElementException("RTO registration not found"));

        // 3️⃣ Map DTO to Entity
        CarListing listing = new CarListing();
        listing.setCarMasterData(carMasterData);
        listing.setSeller(seller);
        listing.setLocation(location);
        listing.setRtoRegistration(rtoRegistration);
        listing.setMileage(dto.getMileage());
        listing.setOwnershipCount(dto.getOwnershipCount());
        listing.setExpectedPrice(dto.getExpectedPrice());
        listing.setNegotiable(dto.isNegotiable());

        // 4️⃣ Save listing
        CarListing saved = carListingRepository.save(listing);

        // 5️⃣ Map entity → response
        return modelMapper.map(saved, CarListingResponseDto.class);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new NoSuchElementException("Authenticated user not found"));
    }
}
