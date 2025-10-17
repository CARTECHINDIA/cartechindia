package com.cartechindia.service.impl;

import com.cartechindia.dto.request.CarMasterDataRequestDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.exception.ResourceAlreadyExistsException;
import com.cartechindia.exception.ResourceNotFoundException;
import com.cartechindia.repository.CarMasterDataRepository;
import com.cartechindia.service.CarMasterDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarMasterDataServiceImpl implements CarMasterDataService {

    private final CarMasterDataRepository repository;

    public CarMasterDataServiceImpl(CarMasterDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public CarMasterDataResponseDto addCarMasterData(CarMasterDataRequestDto dto) {
        repository.findByMakeAndModelAndVariantAndYearOfManufacture(
                dto.getMake(), dto.getModel(), dto.getVariant(), dto.getYearOfManufacture()
        ).ifPresent(c -> {
            throw new ResourceAlreadyExistsException(
                    "Car with same make, model, variant, and year already exists"
            );
        });

        CarMasterData car = new CarMasterData();
        car.setMake(dto.getMake());
        car.setModel(dto.getModel());
        car.setVariant(dto.getVariant());
        car.setYearOfManufacture(dto.getYearOfManufacture());
        car.setFuelType(dto.getFuelType());
        car.setTransmission(dto.getTransmission());
        car.setBodyType(dto.getBodyType());
        car.setColor(dto.getColor());
        car.setDescription(dto.getDescription());
        repository.save(car);
        return toResponseDto(car);
    }

    @Override
    public CarMasterDataResponseDto getCarById(Long id) {
        CarMasterData car = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        return toResponseDto(car);
    }

    @Override
    public Page<CarMasterDataResponseDto> getAllCars(Pageable pageable) {
        Page<CarMasterData> cars = repository.findAll(pageable);
        // Use map to convert each CarMasterData to CarMasterDataResponseDto
        return cars.map(this::toResponseDto);
    }


    private CarMasterDataResponseDto toResponseDto(CarMasterData car) {
        CarMasterDataResponseDto dto = new CarMasterDataResponseDto();
        dto.setId(car.getId());
        dto.setMake(car.getMake());
        dto.setModel(car.getModel());
        dto.setVariant(car.getVariant());
        dto.setYearOfManufacture(car.getYearOfManufacture());
        dto.setFuelType(car.getFuelType());
        dto.setTransmission(car.getTransmission());
        dto.setBodyType(car.getBodyType());
        dto.setColor(car.getColor());
        dto.setDescription(car.getDescription());
        return dto;
    }

}
