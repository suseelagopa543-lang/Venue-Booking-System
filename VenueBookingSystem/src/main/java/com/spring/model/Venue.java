package com.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String area;
    private String city;
    private Integer pin;
    private Double latitude;
    private Double longitude;
    private Double pricePerHour;

    @Enumerated(EnumType.STRING)
    private Status venueStatus = Status.ACTIVE;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

}
