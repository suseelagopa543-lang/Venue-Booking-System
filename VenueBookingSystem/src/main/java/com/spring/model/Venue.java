package com.spring.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer venueId;
    private String venueName;
    private String sportType;
    private String address;
    private String city;
    private String area;
    private Double latitude;
    private Double longitude;
    private Double pricePerHour;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

}
