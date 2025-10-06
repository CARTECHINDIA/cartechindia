package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rto_master",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"stateCode","rtoCode"})})
public class RtoMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stateCode;   // e.g., MP
    private String rtoCode;     // e.g., 09
    private String officeName;  // Indore RTO
    private String city;
    private String state;
}
