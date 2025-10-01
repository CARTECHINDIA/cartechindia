package com.cartechindia.dto;

import com.cartechindia.entity.CarStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CarSellingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String regNumber;

    // Instead of taking brand/model directly, just accept carId
    private Long carId;

    private Integer manufactureYear;
    private Integer kmDriven;
    private String color;
    private Integer owners;
    private BigDecimal price;  // instead of long
    private String health;
    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<MultipartFile> images;

    // Output when retrieving
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls;

    // Extra: read-only car details fetched via JOIN from "cars" table
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String brand;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String model;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String variant;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String fuelType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String transmission;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String bodyType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
