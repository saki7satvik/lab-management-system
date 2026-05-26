package com.return_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.return_service.enums.OverallCondition;
import com.return_service.enums.ReturnStatus;

@Entity
@Table(name = "returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // 🔗 External reference (no JPA relation)
    @Column(nullable = false)
    private String requestId;

    // 👤 Student who initiated return (JWT)
    @Column(nullable = false)
    private String returnedBy;

    // 👨‍🏫 Instructor who inspected
    private String inspectedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status;

    @Enumerated(EnumType.STRING)
    private OverallCondition overallCondition;

    @Column(nullable = false)
    private boolean fineGenerated;

    private String remarks;

    private LocalDateTime createdAt;
    private LocalDateTime inspectedAt;

    // 🔥 Child mapping
    @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnItem> items;

    // 🔥 helper method (important for JPA consistency)
    public void addItem(ReturnItem item) {

        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        item.setReturnEntity(this);
        this.items.add(item);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = ReturnStatus.INITIATED;
        this.fineGenerated = false;
    }
}
