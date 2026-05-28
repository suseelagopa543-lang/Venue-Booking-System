package com.spring.request;

import com.spring.model.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class VenueDTO {
    private Integer venueId;
    private String venueName;
    private String sportType;
    private String address;
    private String city;
    private String area;
    private int pin;

    private Double pricePerHour;
    private Status venueStatus;


}
