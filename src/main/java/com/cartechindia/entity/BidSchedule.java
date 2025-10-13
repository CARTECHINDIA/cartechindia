package com.cartechindia.entity;

import com.cartechindia.constraints.BidScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bid-schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long biddingId;

    @OneToOne
    @JoinColumn(name = "car_id", nullable = false)
    private CarListing carListing;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal minIncrement = BigDecimal.valueOf(1000);

    // Campaign start time (provided by user)
    @Column(nullable = false)
    private LocalDateTime startTime;

    // Overall end time of the campaign (provided by user)
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BidScheduleStatus status = BidScheduleStatus.SCHEDULED;

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    // =============================
    // 3-Hour Daily Bidding Window
    // =============================
    @Transient
    public LocalDateTime getTodayStartTime() {
        LocalDateTime now = LocalDateTime.now();

        // Campaign not started yet → return first day startTime
        if (now.isBefore(startTime)) {
            return startTime.withSecond(0).withNano(0);
        }

        // Campaign over → return endTime
        if (now.isAfter(endTime)) {
            return endTime.withSecond(0).withNano(0);
        }

        // Otherwise, calculate today's window based on startTime's hour & minute
        return now.withHour(startTime.getHour())
                .withMinute(startTime.getMinute())
                .withSecond(0)
                .withNano(0);
    }

    @Transient
    public LocalDateTime getTodayEndTime() {
        LocalDateTime todayStart = getTodayStartTime();
        LocalDateTime calculatedEnd = todayStart.plusHours(3);

        // Ensure end does not exceed overall campaign endTime
        if (endTime != null && calculatedEnd.isAfter(endTime)) {
            return endTime;
        }

        return calculatedEnd;
    }

    @Transient
    public long getTodayDurationMinutes() {
        return java.time.Duration.between(getTodayStartTime(), getTodayEndTime()).toMinutes();
    }

    // =============================
    // Auditing
    // =============================
    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDateTime.now();
        if (this.status == null) this.status = BidScheduleStatus.SCHEDULED;
        if (this.minIncrement == null) this.minIncrement = BigDecimal.valueOf(1000);
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
