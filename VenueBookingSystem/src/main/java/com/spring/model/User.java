package com.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String userName;

    @Column(unique = true)
    private String userEmail;

    private String userPassword;

    @Enumerated(EnumType.STRING)
    private  Status userStatus;

    @Column(unique = true)
    private String phoneNumber;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

}


