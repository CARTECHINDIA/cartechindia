package com.cartechindia.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ImageDto {
    private Long imageId;
    private String imageName;
    private LocalDateTime createdAt;
}
