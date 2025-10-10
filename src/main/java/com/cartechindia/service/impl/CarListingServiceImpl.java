package com.cartechindia.service.impl;

import com.cartechindia.constraints.CarStatus;
import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarImage;
import com.cartechindia.entity.CarListing;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.repository.CarListingRepository;
import com.cartechindia.repository.CarMasterDataRepository;
import com.cartechindia.service.CarListingService;
import com.cartechindia.service.MapperService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class CarListingServiceImpl implements CarListingService {

    private final CarListingRepository carListingRepository;
    private final CarMasterDataRepository carMasterDataRepository;
    private final MapperService mapperService;


    @Value("${app.upload.dir}")
    private String uploadDir;

    // ========================
    // Add Car (Pending by default)
    // ========================
    @Transactional
    @Override
    public CarListingResponseDto addCar(CarListingRequestDto dto) {
        // Check if CarMasterData exists
        CarMasterData masterData = carMasterDataRepository.findById(dto.getCarMasterDataId())
                .orElseThrow(() -> new EntityNotFoundException("CarMasterData not found with id: " + dto.getCarMasterDataId()));

        // Map request to entity
        CarListing carListing = new CarListing();
        carListing.setRegNumber(dto.getRegNumber());
        carListing.setCarMasterDataId(masterData.getId());
        carListing.setManufactureYear(dto.getManufactureYear());
        carListing.setKmDriven(dto.getKmDriven());
        carListing.setColor(dto.getColor());
        carListing.setOwners(dto.getOwners());
        carListing.setPrice(dto.getPrice());
        carListing.setHealth(dto.getHealth());
        carListing.setInsurance(dto.getInsurance());
        carListing.setRegistrationDate(dto.getRegistrationDate());
        carListing.setState(dto.getState());
        carListing.setCity(dto.getCity());
        carListing.setStatus("PENDING");
        carListing.setIsApproved(CarStatus.PENDING);
        carListing.setDeleted(false);

        // Save images
        List<CarImage> imageList = saveImages(dto, carListing);
        carListing.setImages(imageList);

        CarListing saved = carListingRepository.save(carListing);

        // Map to DTO using MapperService
        return mapperService.toCarListingDto(saved, masterData);
    }

    // ========================
    // Get all cars (Approved only)
    // ========================
    @Override
    public Page<CarListingResponseDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CarListing> results = carListingRepository.findAllByDeletedFalse(pageable);

        // Fetch all CarMasterData to map
        List<Long> masterIds = results.stream().map(CarListing::getCarMasterDataId).toList();
        List<CarMasterData> masterDataList = carMasterDataRepository.findAllById(masterIds);

        List<CarListingResponseDto> dtos = mapperService.toCarListingDtoList(results.getContent(), masterDataList);
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ========================
    // Get Car by ID
    // ========================
    @Override
    @Transactional(readOnly = true)
    public CarListingResponseDto getCarById(Long id) {
        CarListing car = carListingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        CarMasterData masterData = carMasterDataRepository.findById(car.getCarMasterDataId())
                .orElse(null);

        return mapperService.toCarListingDto(car, masterData);
    }

    // ========================
    // Pending Cars
    // ========================
    @Override
    public List<CarListingResponseDto> getAllPendingCars() {
        List<CarListing> pendingCars = carListingRepository.findAllPendingListings();

        List<Long> masterIds = pendingCars.stream().map(CarListing::getCarMasterDataId).toList();
        List<CarMasterData> masterDataList = carMasterDataRepository.findAllById(masterIds);

        return mapperService.toCarListingDtoList(pendingCars, masterDataList);
    }

    @Override
    public CarListingResponseDto getPendingCarById(Long id) {
        CarListing car = carListingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Pending car not found with id: " + id));

        if (car.getIsApproved() != CarStatus.PENDING) {
            throw new IllegalArgumentException("Car is not in pending state");
        }

        CarMasterData masterData = carMasterDataRepository.findById(car.getCarMasterDataId())
                .orElse(null);

        return mapperService.toCarListingDto(car, masterData);
    }

    // ========================
    // Approve / Reject
    // ========================
    @Transactional
    @Override
    public void approveCar(Long id) {
        CarListing car = carListingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.APPROVED);
        car.setStatus("APPROVED");
    }

    @Transactional
    @Override
    public void rejectCar(Long id) {
        CarListing car = carListingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.REJECTED);
        car.setStatus("REJECTED");
    }

    // ========================
    // Soft Delete
    // ========================
    @Transactional
    @Override
    public void softDeleteCar(Long id) {
        CarListing car = carListingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setDeleted(true);
    }

    // ========================
    // Save images helper
    // ========================
    private List<CarImage> saveImages(CarListingRequestDto dto, CarListing car) {
        List<CarImage> imageList = new ArrayList<>();
        if (dto.getImages() != null) {
            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.createDirectories(filePath.getParent());
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        CarImage img = new CarImage();
                        img.setFileName(file.getOriginalFilename());
                        img.setFileType(file.getContentType());
                        img.setFilePath("/uploads/" + fileName);
                        img.setCarListing(car);

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





    // ==============================
    // Brand / Model / Variant methods
    // ==============================
    @Override
    public List<String> getAllBrands() {
        return carListingRepository.findAllDistinctMakes();
    }

    @Override
    public List<String> getModelsByBrand(String brand) {
        return carListingRepository.findModelsByMake(brand);
    }

    @Override
    public List<String> getVariantsByModel(String model) {
        return carListingRepository.findVariantsByModel(model);
    }

    @Override
    public List<CarMasterData> getCarDetailsByVariant(String variant) {
        return carListingRepository.findByVariant(variant);
    }

}
