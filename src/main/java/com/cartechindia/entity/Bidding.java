package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bidding")
public class Bidding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long biddingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private CarSelling carSelling;   // Assuming you have Car entity mapped to CARS table

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal startAmount;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BiddingStatus status = BiddingStatus.SCHEDULED;

    @Column(updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;   // Assuming SIGN_UP table is User entity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
