package com.cartechindia.dto.response;

import com.cartechindia.constraints.CarStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarListingResponseDto {

    private Long id;
    private String regNumber;
    private Integer manufactureYear;
    private Integer kmDriven;
    private String color;
    private Integer owners;

    private BigDecimal price;          // Updated from Double to BigDecimal
    private String health;
    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private CarStatus isApproved;
    private String Status;
    private Boolean deleted;
    private LocalDateTime createdAt;

    // Car Master Data
    private Long carMasterId;
    private String make;
    private String model;
    private String variant;
    private Integer yearOfManufacture;
    private String fuelType;
    private String transmission;
    private String bodyType;
    private String masterColor;
    private String description;

    // Images
    private List<String> imageUrls;
    private Double latitude;
    private Double longitude;
}
