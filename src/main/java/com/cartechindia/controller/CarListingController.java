package com.cartechindia.controller;

import com.cartechindia.entity.CarListing;
import com.cartechindia.service.CarListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarListingController {

    private final CarListingService carService;

    @GetMapping
    public List<CarListing> getAll() { return carService.getAll(); }

    @PostMapping
    //@PreAuthorize("hasRole('DEALER')")
    public CarListing create(@RequestBody CarListing car) { return carService.save(car); }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public CarListing update(@PathVariable Long id, @RequestBody CarListing car) {
        car.setId(id);
        return carService.save(car);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('DEALER')")
    public void delete(@PathVariable Long id) { carService.delete(id); }
}
