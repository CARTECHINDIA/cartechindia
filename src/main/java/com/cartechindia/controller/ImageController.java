package com.cartechindia.controller;

import com.cartechindia.dto.ImageDto;
import com.cartechindia.entity.User;
import com.cartechindia.repository.UserRepository;
import com.cartechindia.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("image")
public class ImageController {

    private final ImageService imageService;
    private final UserRepository userRepository;

    public ImageController(ImageService imageService, UserRepository userRepository) {
        this.imageService = imageService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER', 'SELLER')")
    @PostMapping(value = "/upload/{carSellingId}", consumes = "multipart/form-data")
    public ResponseEntity<List<ImageDto>> uploadMultipleImages(
            @PathVariable Long carSellingId,
            @RequestParam("files") List<MultipartFile> files) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // comes from your UserDetailsService

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<ImageDto> images = imageService.saveImages(user.getId(), carSellingId, files);
        return ResponseEntity.ok(images);
    }
}
