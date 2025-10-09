package com.cartechindia.service;

import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.entity.Location;
import com.cartechindia.entity.RtoRegistration;
import com.cartechindia.entity.User;
import com.cartechindia.repository.*;
import com.cartechindia.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarListingService {

    private final CarListingRepository carListingRepository;
    private final CarMasterDataRepository carMasterDataRepository;
    private final LocationRepository locationRepository;
    private final RTORegistrationRepository rtoRegistrationRepository;
    private final AuthUtil authUtil;
    private final MapperService mapperService;

    // ----------------------
    public CarListingResponseDto createCarListing(CarListingRequestDto dto) {
        // 1️⃣ Fetch authenticated user (seller)
        User seller = authUtil.getCurrentUser();

        // 2️⃣ Fetch nested entities
        CarMasterData carMaster = carMasterDataRepository.findById(dto.getCarMasterId())
                .orElseThrow(() -> new NoSuchElementException("Car master data not found"));
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new NoSuchElementException("Location not found"));
        RtoRegistration rto = rtoRegistrationRepository.findById(dto.getRtoRegistrationId())
                .orElseThrow(() -> new NoSuchElementException("RTO registration not found"));

        // 3️⃣ Map DTO → Entity
        CarListing listing = new CarListing();
        listing.setSeller(seller);
        listing.setCarMasterData(carMaster);
        listing.setLocation(location);
        listing.setRtoRegistration(rto);
        listing.setMileage(dto.getMileage());
        listing.setOwnershipCount(dto.getOwnershipCount());
        listing.setExpectedPrice(dto.getExpectedPrice());
        listing.setNegotiable(dto.isNegotiable());

        // 4️⃣ Save
        CarListing saved = carListingRepository.save(listing);

        // 5️⃣ Map → Response DTO using MapperService
        return mapperService.toCarListingDto(saved);
    }

    // Other CRUD methods can remain as-is or use mapperService for responses
}
