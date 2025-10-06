package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_images")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id", nullable = false)
    private CarListing carListing;

    private String imagePath; // file path or URL to S3 / CDN

    @Column(nullable = false)
    private boolean isPrimary = false; // first image is primary thumbnail

    private String description; // optional
}
