package com.vestingCustodyApp.vca.entity;

import com.vestingCustodyApp.vca.enums.MediaOrigin;
import com.vestingCustodyApp.vca.enums.MediaType;
import com.vestingCustodyApp.vca.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String mediaName;

    @Enumerated(value = EnumType.STRING)
    private MediaType mediaType;

    @Enumerated(value = EnumType.STRING)
    private MediaOrigin origin;

    private String tool;

    private String purpose;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private String hash;

    private String publicToken;

    @ManyToOne
    private Review review;
}
