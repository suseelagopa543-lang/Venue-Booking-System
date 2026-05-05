package com.spring.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VenuePaymentSummary {

        private Integer venueId;
        private String venueName;
        private Double totalAmount;

        public VenuePaymentSummary(Integer venueId, String venueName, Double totalAmount) {
            this.venueId = venueId;
            this.venueName = venueName;
            this.totalAmount = totalAmount;
        }

}
