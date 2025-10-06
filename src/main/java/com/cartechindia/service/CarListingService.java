package com.cartechindia.service;

import com.cartechindia.entity.CarListing;
import com.cartechindia.repository.CarListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarListingService {
    private final CarListingRepository carListingRepository;

    public List<CarListing> getAll() { return carListingRepository.findAll(); }
    public CarListing getById(Long id) { return carListingRepository.findById(id).orElse(null); }

    public CarListing save(CarListing car) {
        if (!car.isDeleted()) car.setDeleted(false);
        return carListingRepository.save(car);
    }

    public void delete(Long id) {
        CarListing car = getById(id);
        if (car != null) {
            car.setDeleted(true);
            carListingRepository.save(car);
        }
    }
}
