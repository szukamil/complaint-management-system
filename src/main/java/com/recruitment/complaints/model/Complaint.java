package com.recruitment.complaints.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reporter", nullable = false)
    private String reporter;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "counter", nullable = false)
    private Integer counter;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (counter == null) {
            counter = 1;
        }
    }

    public void incrementCounter() {
        this.counter += 1;
    }
}
