package com.cartechindia.serviceImpl;

import com.cartechindia.entity.Images;
import com.cartechindia.entity.User;
import com.cartechindia.repository.ImageRepository;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    // Where files will be stored on your EC2 instance
    @Value("${app.upload.dir:/opt/cartech/uploads}")
    private String uploadDir;

    public ImageServiceImpl(ImageRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String saveImage(Long userId, MultipartFile file) {
        try {
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            // Ensure upload dir exists
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Generate unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // Save file physically
            Files.copy(file.getInputStream(), filePath);

            // Save only path & metadata in DB
            Images image = new Images();
            image.setImageName(filePath.toString());
            image.setUser(user);

            imageRepository.save(image);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
}
