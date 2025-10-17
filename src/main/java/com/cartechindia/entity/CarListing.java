package com.cartechindia.entity;

import com.cartechindia.constraints.CarStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "car-listing")
public class CarListing extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String regNumber;

    @Column(name = "car_master_id", nullable = false)
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

    @OneToMany(mappedBy = "carListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CarImage> images;

    // Location of the car posted by seller
    private Double latitude;
    private Double longitude;

    @PrePersist
    public void onCreate() {
        this.setCreatedDateTime(LocalDateTime.now());
        this.setUpdatedDateTime(LocalDateTime.now());
        this.deleted=false;
    }

    @PreUpdate
    public void onUpdate() {
        this.setUpdatedDateTime(LocalDateTime.now());
    }
}
