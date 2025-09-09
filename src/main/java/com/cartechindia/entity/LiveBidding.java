package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_bidding")
@Data
public class LiveBidding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long liveBidId;

    @ManyToOne
    @JoinColumn(name = "bidding_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_live_bidding_bidding"))
    private Bidding bidding;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_live_bidding_user"))
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal bidAmount;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime bidTime;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
