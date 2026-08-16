package com.vestingCustodyApp.vca.entity;

import com.vestingCustodyApp.vca.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Media media;

    @ManyToOne
    private User reviewer;

    private Instant reviewDate;

    private String observation;

    private Status status;
}
