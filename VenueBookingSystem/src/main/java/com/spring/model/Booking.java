package com.spring.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;
    private LocalDateTime bookingTime;


    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "booking_slots",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "slot_id")
    )
    private List<Slot> slots;

    @ManyToOne
    @JoinColumn(name = "venue_Id")
    private Venue venue;


}
