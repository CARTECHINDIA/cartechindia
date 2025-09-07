package com.cartechindia.serviceImpl;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.CarSellingService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarSellingServiceImpl implements CarSellingService {

    private final CarSellingRepository repository;
    private final ModelMapper modelMapper;

    public CarSellingServiceImpl(CarSellingRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CarSellingDto addCar(CarSellingDto dto) {
        CarSelling carSelling = modelMapper.map(dto, CarSelling.class);
        CarSelling saved = repository.save(carSelling);
        return modelMapper.map(saved, CarSellingDto.class);
    }

    @Override
    public Page<CarSellingDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarSelling> carPage = repository.findAll(pageable);
        return carPage.map(car -> modelMapper.map(car, CarSellingDto.class));
    }


}
