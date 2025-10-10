package com.cartechindia.entity;

import com.cartechindia.constraints.CarStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "car-listing")
public class CarListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String regNumber;

    // ✅ Reference to static "cars" table (foreign key)
    @Column(name = "car_id", nullable = false)
    private Long carMasterDataId;

    private Integer manufactureYear;
    private Integer kmDriven;
    private String color;
    private Integer owners;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;


    private String health;

    private String insurance;
    private LocalDate registrationDate;
    private String state;
    private String city;
    private String status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CarStatus isApproved = CarStatus.PENDING;


    @Column(nullable = false)
    private Boolean deleted = false;


    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "carListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CarImage> images;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
