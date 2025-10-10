package com.cartechindia.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    @Column(name = "created_date_time", updatable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "updated_date_time")
    private LocalDateTime updatedDateTime;

    @PrePersist
    protected void onCreate() {
        createdDateTime = LocalDateTime.now();
        updatedDateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDateTime = LocalDateTime.now();
    }
}
