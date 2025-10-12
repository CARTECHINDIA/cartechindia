package com.cartechindia.dto.response;

import com.cartechindia.constraints.DocumentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDto {
    private Long id;
    private String type;
    private String filePath;
    private DocumentStatus status;
    private LocalDateTime uploadedAt;
    private Long userId;
}
