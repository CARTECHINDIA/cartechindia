package com.cartechindia.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String saveImage(Long userId, MultipartFile file);
}
