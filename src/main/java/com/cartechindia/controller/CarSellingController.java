package com.cartechindia.controller;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.dto.PageResponse;
import com.cartechindia.service.CarSellingService;
import com.cartechindia.util.PageResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
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
                    description = "Car details to be added",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
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
    @PostMapping("/add")
    public ResponseEntity<CarSellingDto> addCar(@RequestBody CarSellingDto dto) {
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
}
