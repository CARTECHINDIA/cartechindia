package com.cartechindia.service;

import com.cartechindia.entity.CarDetails;
import com.cartechindia.repository.CarDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarDetailsService {
    private final CarDetailsRepository carDetailsRepository;

    public List<CarDetails> getAll() { return carDetailsRepository.findAll(); }
    public CarDetails getById(Long id) { return carDetailsRepository.findById(id).orElse(null); }
    public CarDetails save(CarDetails carDetails) { return carDetailsRepository.save(carDetails); }
    public void delete(Long id) { carDetailsRepository.deleteById(id); }
}
