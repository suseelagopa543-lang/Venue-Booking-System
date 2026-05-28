package com.spring.request;

import com.spring.model.Venue;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VenueRequest {
    private Venue venue;
    private Integer vendorId;
}