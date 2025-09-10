package com.cartechindia.serviceImpl;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.entity.Images;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.CarSellingService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarSellingServiceImpl implements CarSellingService {

    private final CarSellingRepository carSellingRepository;
    private final ModelMapper modelMapper;

    public CarSellingServiceImpl(CarSellingRepository repository, ModelMapper modelMapper) {
        this.carSellingRepository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CarSellingDto addCar(CarSellingDto dto) {
        CarSelling car = modelMapper.map(dto, CarSelling.class);
        CarSelling saved = carSellingRepository.save(car);
        CarSellingDto out = modelMapper.map(saved, CarSellingDto.class);

        // map images separately (if any)
        out.setImages(saved.getImages().stream()
                .map(Images::getImageName)
                .collect(Collectors.toList()));
        return out;
    }

    @Override
    public Page<CarSellingDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CarSelling> carPage = carSellingRepository.findAllWithImages(pageable);

        return carPage.map(car -> {
            CarSellingDto dto = modelMapper.map(car, CarSellingDto.class);
            dto.setImages(car.getImages().stream()
                    .map(Images::getImageName)
                    .toList());
            return dto;
        });
    }



    @Override
    public List<String> getAllBrands() {
        return carSellingRepository.findAllDistinctBrands();
    }

    @Override
    public List<String> getModelsByBrand(String brand) {
        return carSellingRepository.findModelsByBrand(brand);
    }

    @Override
    public List<String> getVariantsByModel(String model) {
        return carSellingRepository.findVariantsByModel(model);
    }

    @Override
    public List<CarSellingDto> getCarDetailsByVariant(String variant) {
        return carSellingRepository.findByVariant(variant)
                .stream()
                .map(car -> {
                    CarSellingDto dto = modelMapper.map(car, CarSellingDto.class);
                    dto.setImages(car.getImages().stream().map(Images::getImageName).toList());
                    return dto;
                }).toList();
    }
}
