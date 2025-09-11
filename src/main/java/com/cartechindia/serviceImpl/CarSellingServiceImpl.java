package com.cartechindia.serviceImpl;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.entity.Images;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.CarSellingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public CarSellingDto addCar(CarSellingDto dto) {
        CarSelling car = modelMapper.map(dto, CarSelling.class);

        List<Images> imageList = new ArrayList<>();

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile file : dto.getImages()) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);

                        // Ensure directory exists
                        Files.createDirectories(filePath.getParent());

                        // Save file to EC2 folder
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        Images img = new Images();
                        img.setFileName(file.getOriginalFilename());
                        img.setFileType(file.getContentType());
                        img.setFilePath("/uploads/" + fileName); // relative path for serving later
                        img.setCarSelling(car);

                        imageList.add(img);
                    } catch (IOException e) {
                        log.error("Error saving file {} to {}", file.getOriginalFilename(), uploadDir, e);
                        throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        car.setImages(imageList);

        CarSelling saved = carSellingRepository.save(car);

        CarSellingDto out = modelMapper.map(saved, CarSellingDto.class);
        out.setImageUrls(
                saved.getImages().stream()
                        .map(Images::getFilePath)
                        .collect(Collectors.toList())
        );

        return out;
    }


    @Override
    public Page<CarSellingDto> getAllCars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CarSelling> carPage = carSellingRepository.findAllWithImages(pageable);

        return carPage.map(car -> {
            CarSellingDto dto = modelMapper.map(car, CarSellingDto.class);
            dto.setImageUrls(car.getImages().stream()
                    .map(Images::getFilePath)   // use stored file path
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
                    dto.setImageUrls(car.getImages().stream()
                            .map(Images::getFilePath)   // again use file path
                            .toList());
                    return dto;
                }).toList();
    }

}
