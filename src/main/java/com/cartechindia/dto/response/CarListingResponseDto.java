package com.cartechindia.dto.response;

import com.cartechindia.constraints.CarStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarListingResponseDto {

    // === Car Listing Details ===
    private Long id;
    private String regNumber;
    private Integer manufactureYear;
    private Integer kmDriven;
    private String color;
    private Integer owners;
    private BigDecimal price;
    private String health;
    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private String status;
    private CarStatus isApproved;
    private Boolean deleted;
    private LocalDateTime createdAt;

    // === Linked Car Master Data ===
    private Long carMasterId;       // ID of CarMasterData
    private String make;
    private String model;
    private String variant;
    private int yearOfManufacture;
    private String fuelType;
    private String transmission;
    private String bodyType;
    private String masterColor;
    private String description;

    // === Images ===
    private List<String> imageUrls;
}
