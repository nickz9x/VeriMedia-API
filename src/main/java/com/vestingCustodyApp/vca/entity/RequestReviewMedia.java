package com.vestingCustodyApp.vca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RequestReviewMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant requestedReviewDate;
    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media media;
    private String reason;
}
