package com.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vendorId;
    private String businessName;
    private String businessAddress;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus=ApprovalStatus.PENDING;
    @Enumerated(EnumType.STRING)
    private Status vendorStatus=Status.ACTIVE;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Venue> venues;
}
