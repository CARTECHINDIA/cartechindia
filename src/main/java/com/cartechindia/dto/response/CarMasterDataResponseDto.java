package com.cartechindia.dto.response;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CarMasterDataResponseDto {

    private Long id;
    private String make;         // e.g., Maruti, Hyundai
    private String model;        // Swift, Creta
    private String variant;      // VXI, ZDI
    private int yearOfManufacture;
    private String fuelType;     // Petrol, Diesel, Electric, CNG
    private String transmission; // Manual / Automatic
    private String bodyType;     // SUV, Sedan, Hatchback
    private String color;        // White, Black, Silver
    private String description;
}
