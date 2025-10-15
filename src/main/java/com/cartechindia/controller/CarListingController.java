package com.cartechindia.controller;

import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.request.CarListingUpdateRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarMasterData;
import com.cartechindia.service.CarListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Car Listings", description = "APIs for managing car listings, approval, and metadata")
public class CarListingController {

    private final CarListingService carListingService;

    // =========================
    // Add new car
    // =========================
    @Operation(
            summary = "Add a new car listing",
            description = "Allows DEALER, ADMIN, or SELLER to add a new car with images and details.",
            requestBody = @RequestBody(
                    description = "Car details to be added (multipart form data)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = CarListingRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Car successfully added",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CarListingResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – user lacks required role")
            }
    )
    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarListingResponseDto> addCar(@ModelAttribute CarListingRequestDto dto) {
        return ResponseEntity.ok(carListingService.addCar(dto));
    }

    // =========================
    // Get all approved cars (paginated)
    // =========================
    @Operation(
            summary = "Get all approved cars (paginated)",
            description = "Fetch a paginated list of all approved car listings.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of cars",
                            content = @Content(schema = @Schema(implementation = CarListingResponseDto.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<CarListingResponseDto>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(carListingService.getAllCars(page, size));
    }


    @Operation(
            summary = "Update car listing",
            description = "Updates car details. Only provided fields are updated. Supports image upload (multipart form data).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Car fields to update (multipart form data). Fields not provided remain unchanged.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = CarListingUpdateRequestDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CarListingResponseDto.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – user lacks required role"),
                    @ApiResponse(responseCode = "404", description = "Car not found")
            }
    )
    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarListingResponseDto> updateCar(
            @PathVariable Long id,
            @ModelAttribute CarListingUpdateRequestDto dto) {
        CarListingResponseDto updatedCar = carListingService.updateCar(id, dto);
        return ResponseEntity.ok(updatedCar);
    }






    // =========================
    // Get car by ID
    // =========================
    @Operation(
            summary = "Get car by ID",
            description = "Fetch details of a car listing by its unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved car",
                            content = @Content(schema = @Schema(implementation = CarListingResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Car not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CarListingResponseDto> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carListingService.getCarById(id));
    }

    // =========================
    // Soft delete car
    // =========================
    @Operation(
            summary = "Soft delete a car listing",
            description = "Marks a car as deleted without permanently removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Car successfully soft-deleted"),
                    @ApiResponse(responseCode = "404", description = "Car not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCar(@PathVariable Long id) {
        carListingService.softDeleteCar(id);
        return ResponseEntity.noContent().build();
    }


    // =========================
    // Get all brands
    // =========================
    @Operation(
            summary = "Get all car brands",
            description = "Retrieve a list of all available car brands from master data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of brands retrieved")
            }
    )
    @GetMapping("/brands")
    public ResponseEntity<List<String>> getAllBrands() {
        return ResponseEntity.ok(carListingService.getAllBrands());
    }

    // =========================
    // Get models by brand
    // =========================
    @Operation(
            summary = "Get models by brand",
            description = "Retrieve all models available for a specific car brand.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Models retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Brand not found")
            }
    )
    @GetMapping("/brands/{brand}/models")
    public ResponseEntity<List<String>> getModelsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(carListingService.getModelsByBrand(brand));
    }

    // =========================
    // Get variants by model
    // =========================
    @Operation(
            summary = "Get variants by model",
            description = "Retrieve all variants available for a specific model.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variants retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Model not found")
            }
    )
    @GetMapping("/models/{model}/variants")
    public ResponseEntity<List<String>> getVariantsByModel(@PathVariable String model) {
        return ResponseEntity.ok(carListingService.getVariantsByModel(model));
    }

    // =========================
    // Get car details by variant
    // =========================
    @Operation(
            summary = "Get car details by variant",
            description = "Fetch detailed master data for a specific car variant.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Variant details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Variant not found")
            }
    )
    @GetMapping("/variants/{variant}/details")
    public ResponseEntity<List<CarMasterData>> getCarDetailsByVariant(@PathVariable String variant) {
        return ResponseEntity.ok(carListingService.getCarDetailsByVariant(variant));
    }
}
