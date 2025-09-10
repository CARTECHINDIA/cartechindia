package com.cartechindia.service;

import com.cartechindia.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    ImageDto saveImage(Long userId, Long carSellingId, MultipartFile file);
    List<ImageDto> saveImages(Long userId, Long carSellingId, List<MultipartFile> files);
}
