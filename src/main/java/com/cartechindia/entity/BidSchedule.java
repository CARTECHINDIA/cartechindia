package com.cartechindia.entity;

import com.cartechindia.constraints.BidScheduleStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "bid_schedule")
@Getter
@Setter
public class BidSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Car listing the schedule is for
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id", nullable = false)
    private CarListing carListing;

    // Dealer / seller who scheduled the bid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private User dealer;

    // Bid start and end time
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    // Starting bid amount set by dealer
    @Column(nullable = false)
    private Double startingBidAmount;

    // Minimum increment per bid
    @Column(nullable = false)
    private Double bidIncrementAmount;

    // Optional maximum bid ceiling
    private Double maxBidAmount;

    // Auto-extend auction if bid placed in last X minutes (optional)
    private Integer autoExtendMinutes;

    // Whether bidding is currently active
    @Column(nullable = false)
    private boolean isActive = true;

    // Status of schedule
    @Enumerated(EnumType.STRING)
    private BidScheduleStatus status = BidScheduleStatus.SCHEDULED; // SCHEDULED, COMPLETED, CANCELLED
}
