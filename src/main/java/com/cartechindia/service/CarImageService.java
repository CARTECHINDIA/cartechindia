package com.cartechindia.service;

import com.cartechindia.entity.CarImage;
import com.cartechindia.repository.CarImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarImageService {
    private final CarImageRepository carImageRepository;

    public List<CarImage> getAll() { return carImageRepository.findAll(); }
    public CarImage getById(Long id) { return carImageRepository.findById(id).orElse(null); }
    public CarImage save(CarImage img) {
        return carImageRepository.save(img); 
    }

    public void delete(Long id) {
        CarImage img = getById(id);
        if (img != null) {
            carImageRepository.save(img);
        }
    }
}
