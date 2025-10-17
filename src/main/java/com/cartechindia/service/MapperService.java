package com.cartechindia.service;

import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.response.*;
import com.cartechindia.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapperService {


    // ======================================================
    // 1️⃣ CarListing → CarListingResponseDto
    // ======================================================
    public CarListingResponseDto toCarListingResponseDto(CarListing car, CarMasterData masterData) {
        if (car == null) return null;

        CarListingResponseDto dto = new CarListingResponseDto();
        dto.setId(car.getId());
        dto.setRegNumber(car.getRegNumber());
        dto.setManufactureYear(car.getManufactureYear());
        dto.setKmDriven(car.getKmDriven());
        dto.setColor(car.getColor());
        dto.setOwners(car.getOwners());
        dto.setPrice(car.getPrice() != null ? car.getPrice() : BigDecimal.ZERO);
        dto.setHealth(car.getHealth());
        dto.setInsurance(car.getInsurance());
        dto.setRegistrationDate(car.getRegistrationDate());
        dto.setState(car.getState());
        dto.setCity(car.getCity());
        dto.setIsApproved(car.getIsApproved());
        dto.setStatus(car.getStatus());
        dto.setDeleted(car.getDeleted());
        dto.setCreatedAt(car.getCreatedDateTime());

        // Master Data
        if (masterData != null) {
            dto.setCarMasterId(masterData.getId());
            dto.setMake(masterData.getMake());
            dto.setModel(masterData.getModel());
            dto.setVariant(masterData.getVariant());
            dto.setYearOfManufacture(masterData.getYearOfManufacture());
            dto.setFuelType(masterData.getFuelType());
            dto.setTransmission(masterData.getTransmission());
            dto.setBodyType(masterData.getBodyType());
            dto.setMasterColor(masterData.getColor());
            dto.setDescription(masterData.getDescription());
        }

        // Images
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            List<String> imageUrls = car.getImages().stream()
                    .map(CarImage::getFilePath)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        }

        return dto;
    }

    // ======================================================
    // 2️⃣ List<CarListing> → List<CarListingResponseDto>
    // ======================================================
    public List<CarListingResponseDto> toCarListingResponseDtoList(
            List<CarListing> listings, List<CarMasterData> masterDataList) {

        Map<Long, CarMasterData> masterMap = masterDataList.stream()
                .collect(Collectors.toMap(CarMasterData::getId, m -> m));

        return listings.stream()
                .map(car -> toCarListingResponseDto(car, masterMap.get(car.getCarMasterDataId())))
                .collect(Collectors.toList());
    }

    // ======================================================
    // 3️⃣ CarListingRequestDto → CarListing
    // ======================================================
    public CarListing toCarListingEntity(CarListingRequestDto dto) {
        if (dto == null) return null;

        CarListing entity = new CarListing();
        entity.setId(dto.getId());
        entity.setRegNumber(dto.getRegNumber());
        entity.setKmDriven(dto.getKmDriven());
        entity.setColor(dto.getColor());
        entity.setOwners(dto.getOwners());
        entity.setPrice(dto.getPrice() != null ? dto.getPrice() : BigDecimal.ZERO);
        entity.setHealth(dto.getHealth());
        entity.setInsurance(dto.getInsurance());
        entity.setRegistrationDate(dto.getRegistrationDate());
        entity.setState(dto.getState());
        entity.setCity(dto.getCity());
        entity.setCarMasterDataId(dto.getCarMasterDataId());

        return entity;
    }

    // ======================================================
    // 4️⃣ CarMasterData → CarMasterDataResponseDto
    // ======================================================
    public CarMasterDataResponseDto toCarMasterDataResponseDto(CarMasterData master) {
        if (master == null) return null;

        CarMasterDataResponseDto dto = new CarMasterDataResponseDto();
        dto.setId(master.getId());
        dto.setMake(master.getMake());
        dto.setModel(master.getModel());
        dto.setVariant(master.getVariant());
        dto.setYearOfManufacture(master.getYearOfManufacture());
        dto.setFuelType(master.getFuelType());
        dto.setTransmission(master.getTransmission());
        dto.setBodyType(master.getBodyType());
        dto.setColor(master.getColor());
        dto.setDescription(master.getDescription());
        return dto;
    }

}
