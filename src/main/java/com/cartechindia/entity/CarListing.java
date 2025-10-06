package com.cartechindia.entity;

import com.cartechindia.constraints.CarStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_listing")
public class CarListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_master_id", nullable = false)
    private CarMasterData carMasterData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rto_registration_id", nullable = false)
    private RtoRegistration rtoRegistration;

    private int mileage;
    private int ownershipCount;

    private Double expectedPrice;
    private boolean negotiable;

    @Enumerated(EnumType.STRING)
    private CarStatus status = CarStatus.ACTIVE;

    @Column(nullable = false)
    private boolean isDeleted = false;
}
