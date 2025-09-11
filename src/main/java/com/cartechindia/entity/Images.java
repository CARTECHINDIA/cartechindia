package com.cartechindia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "images")
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;    // original file name
    private String fileType;    // jpg/png/etc.
    private String filePath;    // path on EC2 or accessible URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_selling_id")
    @JsonBackReference
    private CarSelling carSelling;
}
