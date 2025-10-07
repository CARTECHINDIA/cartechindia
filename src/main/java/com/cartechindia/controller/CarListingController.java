package com.cartechindia.controller;

import com.cartechindia.dto.request.CarListingRequestDto;
import com.cartechindia.dto.response.CarListingResponseDto;
import com.cartechindia.entity.CarListing;
import com.cartechindia.service.CarListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarListingController {

    private final CarListingService carListingService;

    @GetMapping
    public List<CarListing> getAll() { return carListingService.getAll(); }

//    @PostMapping
//    //@PreAuthorize("hasRole('DEALER')")
//    public CarListing create(@RequestBody CarListing car) { return carListingService.save(car); }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public CarListing update(@PathVariable Long id, @RequestBody CarListing car) {
        car.setId(id);
        return carListingService.save(car);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public void delete(@PathVariable Long id) { carListingService.delete(id); }

    //====================================

    @PostMapping("add")
    public ResponseEntity<CarListingResponseDto> createCarListing(
            @Valid @RequestBody CarListingRequestDto dto) {

        CarListingResponseDto response = carListingService.createCarListing(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
