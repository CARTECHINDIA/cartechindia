package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_master_data",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"make","model","variant","yearOfManufacture"})})
public class CarMasterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;         // e.g., Maruti, Hyundai
    private String model;        // Swift, Creta
    private String variant;      // VXI, ZDI
    private int yearOfManufacture;

    private String fuelType;     // Petrol, Diesel, Electric, CNG
    private String transmission; // Manual / Automatic
    private String bodyType;     // SUV, Sedan, Hatchback
    private String color;        // White, Black, Silver

    @Column(length = 500)
    private String description; // Optional static description
}
