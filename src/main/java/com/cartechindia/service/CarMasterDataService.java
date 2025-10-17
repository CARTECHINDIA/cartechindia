package com.cartechindia.service;

import com.cartechindia.dto.request.CarMasterDataRequestDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.entity.CarMasterData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarMasterDataService {
    CarMasterDataResponseDto addCarMasterData(CarMasterDataRequestDto dto);
    CarMasterDataResponseDto getCarById(Long id);

    Page<CarMasterDataResponseDto> getAllCars(Pageable pageable);

}
