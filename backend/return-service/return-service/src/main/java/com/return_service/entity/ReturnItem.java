package com.return_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.return_service.enums.ItemCondition;

@Entity
@Table(name = "return_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // 🔗 Parent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id", nullable = false)
    private Return returnEntity;

    // 🔗 External reference
    @Column(nullable = false)
    private String requestItemId;
    
    @Column(nullable = false)
    private String componentId;

    @Column(nullable = false)
    private int quantityReturned;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition")
    private ItemCondition condition;

    private String damageRemarks;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
