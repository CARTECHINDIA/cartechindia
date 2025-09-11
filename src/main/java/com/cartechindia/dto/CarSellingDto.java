package com.cartechindia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CarSellingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long sellingId;

    private String regNumber;
    private String brand;
    private String variant;
    private String model;
    private Integer manufactureYear;
    private String fuelType;
    private String transmission;
    private Integer kmDriven;
    private String bodyType;
    private String color;
    private Integer owners;
    private BigDecimal price;
    private String condition;
    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private String status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<MultipartFile> images;

    // output when retrieving
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> imageUrls;

}
