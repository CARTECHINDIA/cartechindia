package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "live_bid")
public class LiveBid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bid schedule reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bid_schedule_id", nullable = false)
    private BidSchedule bidSchedule;

    // Car listing reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_listing_id", nullable = false)
    private CarListing carListing;

    // Buyer who placed the bid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    // Amount bid
    @Column(nullable = false)
    private Double bidAmount;

    // Time of the bid
    @Column(nullable = false)
    private LocalDateTime bidTime = LocalDateTime.now();

    // Winner flag
    @Column(nullable = false)
    private boolean isWinner = false;

    // Optional: flag for soft delete (archived bids)
    @Column(nullable = false)
    private boolean isDeleted = false;
}
