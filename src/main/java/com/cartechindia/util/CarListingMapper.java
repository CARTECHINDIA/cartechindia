package com.cartechindia.util;

import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.CarListing;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarListingMapper {

    private final ModelMapper modelMapper;

    public CarListingResponseDto toResponseDto(CarListing source) {
        if (source == null) return null;

        CarListingResponseDto destination = modelMapper.map(source, CarListingResponseDto.class);

        // Manually map nested objects
        if (source.getCarMasterData() != null) {
            CarMasterDataResponseDto carMasterDto =
                    modelMapper.map(source.getCarMasterData(), CarMasterDataResponseDto.class);
            destination.setCarMasterData(carMasterDto);
        }

        if (source.getSeller() != null) {
            destination.setSellerId(source.getSeller().getId());
            destination.setSellerName(source.getSeller().getFirstName() + " " + source.getSeller().getLastName());
        }

        if (source.getLocation() != null) {
            destination.setLocationId(source.getLocation().getId());
            destination.setLocationName(source.getLocation().getArea());
        }

        if (source.getRtoRegistration() != null) {
            destination.setRtoRegistrationId(source.getRtoRegistration().getId());
            destination.setRtoRegistrationNumber(source.getRtoRegistration().getRegistrationNumber());
        }

        return destination;
    }
}
