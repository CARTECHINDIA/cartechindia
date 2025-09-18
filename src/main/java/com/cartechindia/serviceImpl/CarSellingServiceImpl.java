package com.cartechindia.service.impl;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.entity.Images;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.CarSellingService;
import com.cartechindia.util.CarSellingProjection;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CarSellingServiceImpl implements CarSellingService {

    private final CarSellingRepository carSellingRepository;
    private final ModelMapper modelMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public CarSellingServiceImpl(CarSellingRepository repository, ModelMapper modelMapper) {
        this.carSellingRepository = repository;
        this.modelMapper = modelMapper;
    }

    // ========================
    // Add Car
    // ========================
    @Transactional
    @Override
    public CarSellingDto addCar(CarSellingDto dto) {
        CarSelling car = modelMapper.map(dto, CarSelling.class);

        // Save images
        List<Images> imageList = saveImages(dto, car);
        car.setImages(imageList);

        CarSelling saved = carSellingRepository.save(car);

        // Fetch full details from projection
        CarSellingProjection projection = carSellingRepository.findCarSellingWithDetails(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("Car details not found for carId: " + saved.getId()));

        CarSellingDto out = mapProjectionToDto(projection);
        out.setImageUrls(saved.getImages().stream().map(Images::getFilePath).toList());
        return out;
    }

    // ========================
    // Get All Cars (Paginated)
    // ========================
    @Override
    public Page<CarSellingDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CarSellingProjection> results = carSellingRepository.findAllWithDetails(pageable);
        List<CarSellingDto> dtos = results.map(this::mapProjectionToDto).toList();
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ========================
    // Brands / Models / Variants
    // ========================
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
        List<CarSellingProjection> results = carSellingRepository.findByVariant(variant);
        return results.stream().map(this::mapProjectionToDto).toList();
    }

    // ========================
    // Map Projection -> DTO
    // ========================
    private CarSellingDto mapProjectionToDto(CarSellingProjection projection) {
        CarSellingDto dto = new CarSellingDto();

        dto.setId(projection.getId());
        dto.setRegNumber(projection.getRegNumber());
        dto.setCarId(projection.getCarId());
        dto.setManufactureYear(projection.getManufactureYear());
        dto.setKmDriven(projection.getKmDriven());
        dto.setColor(projection.getColor());
        dto.setOwners(projection.getOwners());
        dto.setPrice(projection.getPrice());
        dto.setHealth(projection.getHealth());
        dto.setInsurance(projection.getInsurance());
        dto.setRegistrationDate(projection.getRegistrationDate());
        dto.setState(projection.getState());
        dto.setCity(projection.getCity());
        dto.setStatus(projection.getStatus());

        // Cars joined fields
        dto.setBrand(projection.getBrand());
        dto.setModel(projection.getModel());
        dto.setVariant(projection.getVariant());
        dto.setFuelType(projection.getFuelType());
        dto.setTransmission(projection.getTransmission());
        dto.setBodyType(projection.getBodyType());
        dto.setCreatedAt(projection.getCreatedAt());

        // Fetch images from entity relation
        carSellingRepository.findById(dto.getId())
                .ifPresent(carEntity -> dto.setImageUrls(
                        carEntity.getImages().stream().map(Images::getFilePath).toList()
                ));

        return dto;
    }

    // ========================
    // Helpers
    // ========================
    private List<Images> saveImages(CarSellingDto dto, CarSelling car) {
        List<Images> imageList = new ArrayList<>();
        if (dto.getImages() != null) {
            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.createDirectories(filePath.getParent());
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        Images img = new Images();
                        img.setFileName(file.getOriginalFilename());
                        img.setFileType(file.getContentType());
                        img.setFilePath("/uploads/" + fileName);
                        img.setCarSelling(car);

                        imageList.add(img);
                    } catch (IOException e) {
                        log.error("Failed to save image {}", file.getOriginalFilename(), e);
                        throw new IllegalArgumentException("Failed to save image: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }
        return imageList;
    }
}
