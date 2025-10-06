package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rto_registration",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"registrationNumber"})})
public class RtoRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String registrationNumber; // e.g., MP09-AB-1234

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rto_master_id", nullable = false)
    private RtoMaster rtoMaster;
}
