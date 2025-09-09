package com.cartechindia.service;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarSellingService {
    CarSellingDto addCar(CarSellingDto dto);
    Page<CarSellingDto> getAllCars(int page, int size);

    List<String> getAllBrands();
    List<String> getModelsByBrand(String brand);
    List<String> getVariantsByModel(String model);
    List<CarSelling> getCarDetailsByVariant(String variant);

}
