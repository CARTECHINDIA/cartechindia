package com.cartechindia.service;

import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperService {

    private final ModelMapper modelMapper;

    // =========================
    // CarMasterData → DTO
    public CarMasterDataResponseDto toCarMasterDataDto(CarMasterData entity) {
        if (entity == null) return null;
        return modelMapper.map(entity, CarMasterDataResponseDto.class);
    }

    // =========================
    // CarListing → Response DTO (includes nested CarMasterData, Seller, Location, RTO)
    public CarListingResponseDto toCarListingDto(CarListing entity) {
        if (entity == null) return null;

        CarListingResponseDto dto = modelMapper.map(entity, CarListingResponseDto.class);

        // Nested mappings
        dto.setCarMasterData(toCarMasterDataDto(entity.getCarMasterData()));
        mapSellerInfo(entity, dto);
        mapLocationInfo(entity, dto);
        mapRtoInfo(entity, dto);

        return dto;
    }

    // =========================
    // Private helper methods
    private void mapSellerInfo(CarListing entity, CarListingResponseDto dto) {
        if (entity.getSeller() != null) {
            dto.setSellerId(entity.getSeller().getId());
            dto.setSellerName(entity.getSeller().getFirstName() + " " + entity.getSeller().getLastName());
        }
    }

    private void mapLocationInfo(CarListing entity, CarListingResponseDto dto) {
        if (entity.getLocation() != null) {
            dto.setLocationId(entity.getLocation().getId());
            dto.setLocationName(entity.getLocation().getArea());
        }
    }

    private void mapRtoInfo(CarListing entity, CarListingResponseDto dto) {
        if (entity.getRtoRegistration() != null) {
            dto.setRtoRegistrationId(entity.getRtoRegistration().getId());
            dto.setRtoRegistrationNumber(entity.getRtoRegistration().getRegistrationNumber());
        }
    }
}
