package com.cartechindia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car-image")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;    // original file name
    private String fileType;    // jpg/png/etc.
    private String filePath;    // path on EC2 or accessible URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id")
    @JsonBackReference
    private CarListing carListing;
}
