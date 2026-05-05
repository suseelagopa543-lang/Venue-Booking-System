package com.spring.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@ToString
public class BookingDTO {

    private Integer bookingId;
    private String bookingStatus;
    private LocalDateTime bookingTime;

    private String userName;
    private String userEmail;
    private String phoneNumber;

    private String venueName;
    private String city;
    private String area;

    private List<SlotDTO> slots;
}