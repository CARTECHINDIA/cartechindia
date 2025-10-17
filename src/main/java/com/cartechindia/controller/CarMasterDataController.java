package com.cartechindia.controller;

import com.cartechindia.dto.request.CarMasterDataRequestDto;
import com.cartechindia.dto.response.CarMasterDataResponseDto;
import com.cartechindia.service.CarMasterDataService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/car-master")
public class CarMasterDataController {

    private final CarMasterDataService service;

    public CarMasterDataController(CarMasterDataService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<CarMasterDataResponseDto> addCar(@Valid @RequestBody CarMasterDataRequestDto dto) {
        CarMasterDataResponseDto savedCar = service.addCarMasterData(dto);
        return ResponseEntity.ok(savedCar);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarMasterDataResponseDto> getCarById(@PathVariable Long id) {
        CarMasterDataResponseDto car = service.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<CarMasterDataResponseDto>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarMasterDataResponseDto> cars = service.getAllCars(pageable);
        return ResponseEntity.ok(cars);
    }
}
