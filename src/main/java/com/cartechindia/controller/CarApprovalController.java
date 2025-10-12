package com.cartechindia.controller;

import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.service.CarListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("car-approval")
public class CarApprovalController {

    private final CarListingService carListingService;

    public CarApprovalController(CarListingService carListingService) {
        this.carListingService = carListingService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CarListingResponseDto>> getAllPendingCars() {
        List<CarListingResponseDto> pendingCars = carListingService.getAllPendingCars();
        return ResponseEntity.ok(pendingCars);
    }

    // ========================
    // Get a single pending car by ID
    // ========================
    @GetMapping("/pending/{id}")
    public ResponseEntity<CarListingResponseDto> getPendingCarById(@PathVariable Long id) {
        CarListingResponseDto car = carListingService.getPendingCarById(id);
        return ResponseEntity.ok(car);
    }

    // ========================
    // Approve a car
    // ========================
    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approveCar(@PathVariable Long id) {
        carListingService.approveCar(id);
        return ResponseEntity.ok("Car approved successfully.");
    }

    // ========================
    // Reject a car
    // ========================
    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> rejectCar(@PathVariable Long id) {
        carListingService.rejectCar(id);
        return ResponseEntity.ok("Car rejected successfully.");
    }
}
