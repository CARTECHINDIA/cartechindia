package com.cartechindia.service.impl;

import com.cartechindia.constraints.CarStatus;
import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.request.CarListingUpdateRequestDto;
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
        carListing.setIsApproved(CarStatus.PENDING);
        carListing.setDeleted(false);
        carListing.setStatus(dto.getStatus());
        carListing.setLatitude(dto.getLatitude());
        carListing.setLongitude(dto.getLongitude());

        // Save images
        List<CarImage> imageList = saveImages(dto.getImages(), carListing,false);
        carListing.setImages(imageList);

        CarListing saved = carListingRepository.save(carListing);

        // Map to DTO using MapperService
        return mapperService.toCarListingResponseDto(saved, masterData);
    }

    // ========================
    // Get all cars (Approved only)
    // ========================
    @Override
    public Page<CarListingResponseDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<CarListing> results = carListingRepository.findAllApprovedCars(pageable);

        // Fetch all CarMasterData to map
        List<Long> masterIds = results.stream().map(CarListing::getCarMasterDataId).toList();
        List<CarMasterData> masterDataList = carMasterDataRepository.findAllById(masterIds);

        List<CarListingResponseDto> dtos = mapperService.toCarListingResponseDtoList(results.getContent(), masterDataList);
        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }

    // ========================
    // Get Car by ID
    // ========================
    @Override
    @Transactional(readOnly = true)
    public CarListingResponseDto getCarById(Long id) {
        CarListing car = carListingRepository.findApprovedCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        CarMasterData masterData = carMasterDataRepository.findById(car.getCarMasterDataId())
                .orElse(null);

        return mapperService.toCarListingResponseDto(car, masterData);
    }

    // ========================
    // Pending Cars
    // ========================
    @Override
    public List<CarListingResponseDto> getAllPendingCars() {
        List<CarListing> pendingCars = carListingRepository.findAllPendingListings();

        List<Long> masterIds = pendingCars.stream().map(CarListing::getCarMasterDataId).toList();
        List<CarMasterData> masterDataList = carMasterDataRepository.findAllById(masterIds);

        return mapperService.toCarListingResponseDtoList(pendingCars, masterDataList);
    }

    @Override
    public CarListingResponseDto getPendingCarById(Long id) {
        CarListing car = carListingRepository.findApprovedCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pending car not found with id: " + id));

        if (car.getIsApproved() != CarStatus.PENDING) {
            throw new IllegalArgumentException("Car is not in pending state");
        }

        CarMasterData masterData = carMasterDataRepository.findById(car.getCarMasterDataId())
                .orElse(null);

        return mapperService.toCarListingResponseDto(car, masterData);
    }

    // ========================
    // Approve / Reject
    // ========================
    @Transactional
    @Override
    public void approveCar(Long id) {
        CarListing car = carListingRepository.findPendingCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.APPROVED);
    }

    @Transactional
    @Override
    public void rejectCar(Long id) {
        CarListing car = carListingRepository.findPendingCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setIsApproved(CarStatus.REJECTED);
    }

    // ========================
    // Soft Delete
    // ========================
    @Transactional
    @Override
    public void softDeleteCar(Long id) {
        CarListing car = carListingRepository.findApprovedCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        car.setDeleted(true);
    }

    private List<CarImage> saveImages(List<MultipartFile> files, CarListing car, boolean replaceExisting) {
        List<CarImage> savedImages = new ArrayList<>();

        if (files == null || files.isEmpty()) return savedImages;

        if (replaceExisting) {
            // Remove old images if replacing
            if (car.getImages() != null) {
                car.getImages().clear();
            }
        }

        for (MultipartFile file : files) {
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

                    savedImages.add(img);

                } catch (IOException e) {
                    log.error("Failed to save image {}", file.getOriginalFilename(), e);
                    throw new IllegalArgumentException("Failed to save image: " + file.getOriginalFilename(), e);
                }
            }
        }

        // Add saved images to car
        if (car.getImages() == null) {
            car.setImages(savedImages);
        } else {
            car.getImages().addAll(savedImages);
        }

        return savedImages;
    }


    @Transactional
    @Override
    public CarListingResponseDto updateCar(Long id, CarListingUpdateRequestDto dto) {
        CarListing car = carListingRepository.findApprovedCarById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        // Update only provided fields
        if (dto.getRegNumber() != null) car.setRegNumber(dto.getRegNumber());
        if (dto.getManufactureYear() != null) car.setManufactureYear(dto.getManufactureYear());
        if (dto.getKmDriven() != null) car.setKmDriven(dto.getKmDriven());
        if (dto.getColor() != null) car.setColor(dto.getColor());
        if (dto.getOwners() != null) car.setOwners(dto.getOwners());
        if (dto.getPrice() != null) car.setPrice(dto.getPrice());
        if (dto.getHealth() != null) car.setHealth(dto.getHealth());
        if (dto.getInsurance() != null) car.setInsurance(dto.getInsurance());
        if (dto.getRegistrationDate() != null) car.setRegistrationDate(dto.getRegistrationDate());
        if (dto.getState() != null) car.setState(dto.getState());
        if (dto.getCity() != null) car.setCity(dto.getCity());

        // Update CarMasterData if provided
        CarMasterData masterData = null;
        if (dto.getCarMasterDataId() != null) {
            masterData = carMasterDataRepository.findById(dto.getCarMasterDataId())
                    .orElseThrow(() -> new EntityNotFoundException("CarMasterData not found with id: " + dto.getCarMasterDataId()));
            car.setCarMasterDataId(masterData.getId());
        } else {
            masterData = carMasterDataRepository.findById(car.getCarMasterDataId()).orElse(null);
        }

        // Handle images: add new images without deleting existing
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<CarImage> imageList = saveImages(dto.getImages(), car, false);
            car.getImages().addAll(imageList);
        }

        CarListing saved = carListingRepository.save(car);
        return mapperService.toCarListingResponseDto(saved, masterData);
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
