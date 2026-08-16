package com.vestingCustodyApp.vca.entity;

import com.vestingCustodyApp.vca.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String email;
    private Boolean isActive;
    @Enumerated(value = EnumType.STRING)
    private Role role;
}
