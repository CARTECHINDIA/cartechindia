package com.cartechindia.service;

import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarImage;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapperService {

    // =========================
    // Map single CarListing + CarMasterData → DTO
    // =========================
    public CarListingResponseDto toCarListingDto(CarListing car, CarMasterData masterData) {
        if (car == null) return null;

        CarListingResponseDto dto = new CarListingResponseDto();

        dto.setId(car.getId());
        dto.setRegNumber(car.getRegNumber());
        dto.setManufactureYear(car.getManufactureYear());
        dto.setKmDriven(car.getKmDriven());
        dto.setColor(car.getColor());
        dto.setOwners(car.getOwners());
        dto.setPrice(car.getPrice());
        dto.setHealth(car.getHealth());
        dto.setInsurance(car.getInsurance());
        dto.setRegistrationDate(car.getRegistrationDate());
        dto.setState(car.getState());
        dto.setCity(car.getCity());
        dto.setStatus(car.getStatus());
        dto.setIsApproved(car.getIsApproved());
        dto.setDeleted(car.getDeleted());
        dto.setCreatedAt(car.getCreatedAt());

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

        if (car.getImages() != null && !car.getImages().isEmpty()) {
            List<String> imageUrls = car.getImages().stream()
                    .map(CarImage::getFilePath)
                    .collect(Collectors.toList());
            dto.setImageUrls(imageUrls);
        }

        return dto;
    }

    // =========================
    // Map list of CarListings + CarMasterData → DTO list
    // =========================
    public List<CarListingResponseDto> toCarListingDtoList(List<CarListing> listings, List<CarMasterData> masterDataList) {
        Map<Long, CarMasterData> masterMap = masterDataList.stream()
                .collect(Collectors.toMap(CarMasterData::getId, m -> m));

        return listings.stream()
                .map(car -> toCarListingDto(car, masterMap.get(car.getCarMasterDataId())))
                .collect(Collectors.toList());
    }
}
