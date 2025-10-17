package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "live-bid")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @ManyToOne
    @JoinColumn(name = "bidding_id", nullable = false)
    private BidSchedule bidSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private LocalDateTime bidTime;

    @PrePersist
    public void onCreate() {
        if (this.bidTime == null) this.bidTime = LocalDateTime.now();
    }
}
