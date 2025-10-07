package com.cartechindia.util;

import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.CarMasterData;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CarMasterDataMapper {

    private final ModelMapper modelMapper;

    public CarMasterDataMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Convert a single CarMasterData entity to CarMasterDataResponseDto
     */
    public CarMasterDataResponseDto toDto(CarMasterData entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, CarMasterDataResponseDto.class);
    }

    /**
     * Convert a list of CarMasterData entities to a list of CarMasterDataResponseDto
     */
    public List<CarMasterDataResponseDto> toDtoList(List<CarMasterData> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
