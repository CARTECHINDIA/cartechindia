package com.cartechindia.entity;

import com.cartechindia.constraints.*;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_details")
public class CarDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id", nullable = false)
    private CarListing carListing;

    // Insurance
    private boolean insuranceAvailable;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    private String insuranceExpiry;
    private boolean loanHypothecated;

    // Documents
    private String rcDocumentPath;
    private String pucDocumentPath;

    // Condition / History
    @Enumerated(EnumType.STRING)
    private TyreCondition tyreCondition;

    @Enumerated(EnumType.STRING)
    private AccidentHistory accidentHistory;

    @Enumerated(EnumType.STRING)
    private ServiceHistory serviceHistory;

    @Enumerated(EnumType.STRING)
    private ModificationStatus modifications;
}
