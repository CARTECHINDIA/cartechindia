package com.cartechindia.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "car_selling")
public class CarSelling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellingId;

    @Column(nullable = false, unique = true, length = 20)
    private String regNumber;

    @Column(nullable = false)
    private String brand;

    private String variant;

    @Column(nullable = false)
    private String model;

    private Integer manufactureYear;
    private String fuelType;
    private String transmission;
    private Integer kmDriven;
    private String bodyType;
    private String color;
    private Integer owners;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "car_condition")
    private String condition;

    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private String status;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "carSelling", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Images> images;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
