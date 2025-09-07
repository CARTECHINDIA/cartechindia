package com.cartechindia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

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
}
