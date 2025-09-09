package com.cartechindia.controller;

import com.cartechindia.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("image")
@Tag(name = "Image API", description = "APIs for uploading and managing selling car images")
public class ImageController {

    private final ImageService imageService;
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @Operation(
            summary = "Upload car image",
            description = "Uploads an image for a specific car. The file is stored on EC2, and only the path is saved in the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER', 'SELLER')")
    @PostMapping(value = "/upload/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "User ID who owns this image", required = true)
            @PathVariable Long userId,

            @Parameter(description = "Image file to upload", required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")))
            @RequestParam("file") MultipartFile file
    ) {
        String path = imageService.saveImage(userId, file);
        return ResponseEntity.ok("Image uploaded successfully! Saved at: " + path);
    }

}
