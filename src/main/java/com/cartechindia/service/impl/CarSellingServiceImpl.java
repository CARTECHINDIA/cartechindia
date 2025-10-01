package com.cartechindia.service.impl;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.entity.CarStatus;
import com.cartechindia.entity.Images;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.CarSellingService;
import com.cartechindia.util.CarSellingProjection;
import com.cartechindia.util.CarsProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

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
    // Add Car → default PENDING
    // ========================
    @Transactional
    @Override
    public CarSellingDto addCar(CarSellingDto dto) {
        CarSelling car = modelMapper.map(dto, CarSelling.class);
        car.setStatus("PENDING");

        List<Images> imageList = saveImages(dto, car);
        car.setImages(imageList);

        CarSelling saved = carSellingRepository.save(car);

        CarSellingProjection projection = carSellingRepository.findCarByIdIgnoreIsApproved(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("Car details not found for carId: %d".formatted(saved.getId())));

        CarSellingDto out = mapProjectionToDto(projection);
        out.setImageUrls(saved.getImages().stream().map(Images::getFilePath).toList());
        return out;
    }

    // ========================
    // Get All APPROVED Cars (Paginated)
    // ========================
    @Override
    public Page<CarSellingDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CarSellingProjection> results = carSellingRepository.findAllCarsSelling(pageable);

        List<CarSellingDto> dtos = results.map(this::mapProjectionToDto).toList();
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ========================
    // Get Car by ID (only APPROVED)
    // ========================
    @Override
    @Transactional(readOnly = true)
    public CarSellingDto getCarById(Long id) {
        CarSellingProjection projection = carSellingRepository.findCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        CarSellingDto dto = mapProjectionToDto(projection);

        CarSelling carEntity = carSellingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        if (carEntity.getImages() != null && !carEntity.getImages().isEmpty()) {
            dto.setImageUrls(carEntity.getImages().stream().map(Images::getFilePath).toList());
        }

        return dto;
    }

    // ========================
    // Get Pending Cars for Review
    // ========================
    @Override
    public List<CarSellingDto> getAllPendingCars() {
        return carSellingRepository.findAllPendingCars().stream()
                .map(this::mapProjectionToDto)
                .toList();
    }

    @Override
    public CarSellingDto getPendingCarById(Long id) {
        CarSellingProjection projection = carSellingRepository.findPendingCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pending car not found with id: " + id));
        return mapProjectionToDto(projection);
    }

    // ========================
    // Approve / Reject Car
    // ========================
    @Override
    @Transactional
    public void approveCar(Long id) {
        CarSelling car = carSellingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.APPROVED);
        carSellingRepository.save(car);
    }

    @Override
    @Transactional
    public void rejectCar(Long id) {
        CarSelling car = carSellingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.REJECTED);
        carSellingRepository.save(car);
    }

    // ========================
    // Soft delete
    // ========================
    @Override
    @Transactional
    public void softDeleteCar(Long id) {
        CarSelling existing = carSellingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: %d".formatted(id)));
        existing.setDeleted(true);
        carSellingRepository.save(existing);
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
        if (brand == null) return List.of();
        return carSellingRepository.findModelsByBrand(brand).stream()
                .map(this::capitalize)
                .sorted()
                .toList();
    }

    @Override
    public List<String> getVariantsByModel(String model) {
        if (model == null) return List.of();
        return carSellingRepository.findVariantsByModel(model).stream()
                .map(this::capitalize)
                .sorted()
                .toList();
    }

    @Override
    public List<CarsProjection> getCarDetailsByVariant(String variant) {
        return carSellingRepository.findByVariant(variant);
    }

    // ========================
    // Helpers
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
        dto.setBrand(capitalize(projection.getBrand()));
        dto.setModel(capitalize(projection.getModel()));
        dto.setVariant(capitalize(projection.getVariant()));
        dto.setFuelType(projection.getFuelType());
        dto.setTransmission(projection.getTransmission());
        dto.setBodyType(projection.getBodyType());
        dto.setCreatedAt(projection.getCreatedAt());

        carSellingRepository.findById(projection.getId())
                .ifPresent(car -> dto.setImageUrls(car.getImages().stream().map(Images::getFilePath).toList()));

        return dto;
    }

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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        str = str.toLowerCase();
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
