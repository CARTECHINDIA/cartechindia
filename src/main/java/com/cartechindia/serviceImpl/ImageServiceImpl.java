package com.cartechindia.serviceImpl;

import com.cartechindia.dto.ImageDto;
import com.cartechindia.entity.CarSelling;
import com.cartechindia.entity.Images;
import com.cartechindia.entity.User;
import com.cartechindia.repository.ImageRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.repository.CarSellingRepository;
import com.cartechindia.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final CarSellingRepository carSellingRepository;

    @Value("${app.upload.dir:/opt/cartech/uploads}")
    private String uploadDir;

    public ImageServiceImpl(ImageRepository imageRepository, UserRepository userRepository,
                            CarSellingRepository carSellingRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.carSellingRepository = carSellingRepository;
    }

    private String sanitize(String original) {
        if (original == null) return "file";
        return original.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }

    @Override
    @Transactional
    public ImageDto saveImage(Long userId, Long carSellingId, MultipartFile file) {
        return saveImages(userId, carSellingId, List.of(file)).get(0);
    }

    @Override
    @Transactional
    public List<ImageDto> saveImages(Long userId, Long carSellingId, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        CarSelling car = carSellingRepository.findById(carSellingId)
                .orElseThrow(() -> new RuntimeException("Car not found: " + carSellingId));

        Path carDir = Paths.get(uploadDir, String.valueOf(user.getId()), String.valueOf(car.getSellingId()));
        try {
            if (!Files.exists(carDir)) {
                Files.createDirectories(carDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create directories", e);
        }

        return files.stream().map(f -> {
            String clean = sanitize(f.getOriginalFilename());
            String fileName = System.currentTimeMillis() + "_" + clean;
            Path target = carDir.resolve(fileName);
            try {
                // Replace existing if same name (unlikely due to timestamp)
                Files.copy(f.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new RuntimeException("Failed to store file: " + clean, ioe);
            }

            String relative = "/uploads/" + user.getId() + "/" + car.getSellingId() + "/" + fileName;

            Images img = new Images();
            img.setImageName(relative);
            img.setUser(user);
            img.setCarSelling(car);

            Images saved = imageRepository.save(img);
            ImageDto dto = new ImageDto();
            dto.setImageId(saved.getImageId());
            dto.setImageName(saved.getImageName());
            dto.setCreatedAt(LocalDateTime.now());
            return dto;
        }).collect(Collectors.toList());
    }
}
