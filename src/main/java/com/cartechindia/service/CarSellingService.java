package com.cartechindia.service;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.util.CarsProjection;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarSellingService {
    CarSellingDto addCar(CarSellingDto dto);
    Page<CarSellingDto> getAllCars(int page, int size);

    List<String> getAllBrands();
    List<String> getModelsByBrand(String brand);
    List<String> getVariantsByModel(String model);
    List<CarsProjection> getCarDetailsByVariant(String variant);

    void softDeleteCar(Long id);
    CarSellingDto getCarById(Long id);

    List<CarSellingDto> getAllPendingCars();
    CarSellingDto getPendingCarById(Long id);
    void approveCar(Long id);
    void rejectCar(Long id);


}
