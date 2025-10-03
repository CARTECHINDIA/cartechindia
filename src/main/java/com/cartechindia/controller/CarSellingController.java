package com.cartechindia.controller;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.service.CarSellingService;
import com.cartechindia.util.CarsProjection;
import com.cartechindia.util.PageResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("car")
@Tag(name = "Car Selling", description = "Endpoints for managing car selling operations")
public class CarSellingController {

    private final CarSellingService carSellingService;

    public CarSellingController(CarSellingService carSellingService) {
        this.carSellingService = carSellingService;
    }

    @Operation(
            summary = "Add a new car",
            description = "Allows DEALER, ADMIN, or SELLER to add a new car to the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Car details to be added along with images",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = CarSellingDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Car successfully added",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CarSellingDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – user does not have required role"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarSellingDto> addCar(@ModelAttribute CarSellingDto dto) {
        return ResponseEntity.ok(carSellingService.addCar(dto));
    }


    @Operation(
            summary = "Get all cars (paginated)",
            description = "Fetch a paginated list of cars with metadata like total pages, size, etc.",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)", example = "0"),
                    @Parameter(name = "size", description = "Number of records per page", example = "10")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved paginated list of cars",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'DEALER', 'ADMIN', 'SELLER', 'BUYER')")
    @GetMapping("/all")
    public ResponseEntity<PageResponse<CarSellingDto>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                PageResponseMapper.toPageResponse(carSellingService.getAllCars(page, size))
        );
    }


    @Operation(
            summary = "Get all car brands",
            description = "Returns a list of all available car brands",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of brands fetched successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(example = "Hyundai")))),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @GetMapping("/brands")
    @PreAuthorize("hasAnyRole('USER', 'DEALER', 'ADMIN', 'SELLER', 'BUYER')")
    public List<String> getAllBrands() {
        return carSellingService.getAllBrands();
    }

    @Operation(
            summary = "Get models by brand",
            description = "Returns a list of models for a given brand",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of models fetched successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(example = "i20")))),
                    @ApiResponse(responseCode = "400", description = "Invalid brand name"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @GetMapping("/models")
    @PreAuthorize("hasAnyRole('USER', 'DEALER', 'ADMIN', 'SELLER', 'BUYER')")
    public List<String> getModelsByBrand(
            @RequestParam @Parameter(description = "Car brand (mandatory)", example = "Hyundai") String brand) {
        return carSellingService.getModelsByBrand(brand);
    }

    @Operation(
            summary = "Get variants by model",
            description = "Returns a list of variants for a given model",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of variants fetched successfully",
                            content = @Content(array = @ArraySchema(schema = @Schema(example = "Sportz")))),
                    @ApiResponse(responseCode = "400", description = "Invalid model name"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @GetMapping("/variants")
    @PreAuthorize("hasAnyRole('USER', 'DEALER', 'ADMIN', 'SELLER', 'BUYER')")
    public List<String> getVariantsByModel(
            @RequestParam @Parameter(description = "Car model (mandatory)", example = "i20") String model) {
        return carSellingService.getVariantsByModel(model);
    }


    @Operation(
            summary = "Get car details by variant",
            description = "Returns detailed car information for the given variant",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car details fetched successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CarsProjection.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid variant name"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized")
            }
    )
    @GetMapping("/details")
    @PreAuthorize("hasAnyRole('USER', 'DEALER', 'ADMIN', 'SELLER', 'BUYER')")
    public List<CarsProjection> getCarDetailsByVariant(
            @RequestParam
            @Parameter(description = "Car variant (mandatory)", example = "Sportz") String variant) {

        // Normalize input
        String normalizedVariant = variant.trim().toLowerCase();
        return carSellingService.getCarDetailsByVariant(normalizedVariant);
    }


    @Operation(
            summary = "Soft delete a car",
            description = "Marks a car as deleted instead of removing it permanently."
    )
    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> softDeleteCar(@PathVariable Long id) {
        carSellingService.softDeleteCar(id);
        return ResponseEntity.ok("Car soft deleted successfully.");
    }


    @Operation(summary = "Get car by ID", description = "Fetch a car by ID including images. Only non-deleted cars are returned.")
    @PreAuthorize("hasAnyRole('DEALER', 'ADMIN', 'SELLER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<CarSellingDto> getCarById(@PathVariable Long id) {
        CarSellingDto dto = carSellingService.getCarById(id);
        return ResponseEntity.ok(dto);
    }

}
