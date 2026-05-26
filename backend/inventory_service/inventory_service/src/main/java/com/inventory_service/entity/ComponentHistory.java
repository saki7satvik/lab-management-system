package com.inventory_service.entity;

import java.time.LocalDateTime;

import com.inventory_service.enums.ActionType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComponentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String componentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType action;

    @Column(nullable = false)
    private int quantityChanged;

    @Column(nullable = false)
    private int totalQuantityAfter;

    @Column(nullable = false)
    private int availableQuantityAfter;

    @Column(nullable = false)
    private String performedBy; // collegeId

    private String remarks; // optional (damage, correction, etc.)

    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}