package com.cartechindia.dto.request;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class CarMasterDataRequestDto {

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Variant is required")
    private String variant;

    @Min(value = 1950, message = "Year of manufacture must be >= 1950")
    @Max(value = 2100, message = "Year of manufacture must be <= 2100")
    private int yearOfManufacture;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    @NotBlank(message = "Transmission type is required")
    private String transmission;

    @NotBlank(message = "Body type is required")
    private String bodyType;

    @NotBlank(message = "Color is required")
    private String color;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
