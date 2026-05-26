package com.issue_service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.issue_service.enums.IssueStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // 🔗 Link to Request Service (NO JPA relation)
    @Column(nullable = false)
    private String requestId;

    // 👤 Who issued (collegeId from user service)
    @Column(nullable = false)
    private String issuedBy;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    // 🔗 One-to-many relation
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<IssueItem> items = new ArrayList<>();

    // 🔥 Add helper
    public void addItem(IssueItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setIssue(this);
    }

    // 🔥 Remove helper (IMPORTANT for orphanRemoval)
    public void removeItem(IssueItem item) {
        if (item == null || this.items == null) return;

        if (this.items.remove(item)) {
            item.setIssue(null);
        }
    }

    // 🔥 Optional: clear all (useful for updates)
    public void clearItems() {
        if (this.items != null) {
            for (IssueItem item : this.items) {
                item.setIssue(null);
            }
            this.items.clear();
        }
    }
    
    @PrePersist
    public void prePersist() {
        this.issueDate = LocalDateTime.now();
    }
}
