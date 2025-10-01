package com.cartechindia.controller;

import com.cartechindia.dto.CarSellingDto;
import com.cartechindia.service.CarSellingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("car-approval")
public class CarApprovalController {

    private final CarSellingService carSellingService;

    public CarApprovalController(CarSellingService carSellingService) {
        this.carSellingService = carSellingService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CarSellingDto>> getAllPendingCars() {
        List<CarSellingDto> pendingCars = carSellingService.getAllPendingCars();
        return ResponseEntity.ok(pendingCars);
    }

    // ========================
    // Get a single pending car by ID
    // ========================
    @GetMapping("/pending/{id}")
    public ResponseEntity<CarSellingDto> getPendingCarById(@PathVariable Long id) {
        CarSellingDto car = carSellingService.getPendingCarById(id);
        return ResponseEntity.ok(car);
    }

    // ========================
    // Approve a car
    // ========================
    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approveCar(@PathVariable Long id) {
        carSellingService.approveCar(id);
        return ResponseEntity.ok("Car approved successfully.");
    }

    // ========================
    // Reject a car
    // ========================
    @PatchMapping("/reject/{id}")
    public ResponseEntity<String> rejectCar(@PathVariable Long id) {
        carSellingService.rejectCar(id);
        return ResponseEntity.ok("Car rejected successfully.");
    }
}
