package com.cartechindia.service;

import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.request.CarListingUpdateRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarMasterData;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarListingService {
    CarListingResponseDto addCar(CarListingRequestDto dto);
    Page<CarListingResponseDto> getAllCars(int page, int size);
    CarListingResponseDto updateCar(Long id, CarListingUpdateRequestDto dto);


    List<String> getAllBrands();
    List<String> getModelsByBrand(String brand);
    List<String> getVariantsByModel(String model);
    List<CarMasterData> getCarDetailsByVariant(String variant);

    void softDeleteCar(Long id);
    CarListingResponseDto getCarById(Long id);

    List<CarListingResponseDto> getAllPendingCars();
    CarListingResponseDto getPendingCarById(Long id);
    void approveCar(Long id);
    void rejectCar(Long id);


}
