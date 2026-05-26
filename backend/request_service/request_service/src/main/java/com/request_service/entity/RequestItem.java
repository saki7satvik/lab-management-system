package com.request_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.request_service.enums.RequestItemStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false)
    private String componentId;

    @Column(nullable = false)
    private int quantityRequested;

    @Builder.Default
    @Column(nullable = false)
    private int quantityApproved = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestItemStatus status;

    @PrePersist
    @PreUpdate
    private void validate() {
        if (quantityRequested <= 0) {
            throw new IllegalStateException("Requested quantity must be > 0");
        }
        if (quantityApproved < 0) {
            throw new IllegalStateException("Approved quantity cannot be negative");
        }
    }
}